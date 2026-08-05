package com.demo.weixin.service.assistance;

import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.assistance.DonationApplyDao;
import com.demo.weixin.entity.assistance.DonationApply;
import com.demo.weixin.enums.assistance.DonationStatusEnum;
import com.demo.weixin.enums.assistance.DonationTypeEnum;
import com.demo.weixin.vo.assistance.DonationApplyVO;
import com.demo.weixin.vo.assistance.DonationAuditVO;
import com.demo.weixin.vo.assistance.DonationQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 捐赠申请服务
 * 提供捐赠申请提交、分页查询、审核、捐赠排行等功能。
 * 企业捐赠审核通过后自动更新爱心企业累计捐赠统计。
 */
@Service
@Slf4j
public class DonationService {

    @Autowired
    private DonationApplyDao donationApplyDao;

    @Autowired
    private CharityEnterpriseService charityEnterpriseService;

    @Autowired
    private com.demo.weixin.service.WechatMsgTemplateService wechatMsgTemplateService;

    /**
     * 提交捐赠申请
     * 校验捐赠类型与对应字段：资金捐赠需amount>0，物资捐赠需goodsName非空。
     *
     * @param userId 申请用户ID
     * @param vo     捐赠申请入参
     * @return 创建后的捐赠申请记录
     */
    public DonationApply submitDonation(Long userId, DonationApplyVO vo) {
        // 参数校验：捐赠类型合法性
        DonationTypeEnum typeEnum = DonationTypeEnum.getByCode(vo.getDonationType());
        if (typeEnum == null) {
            throw new BizException("无效的捐赠类型");
        }
        // 资金捐赠校验金额，物资捐赠校验物资名称
        if (typeEnum == DonationTypeEnum.MONEY) {
            if (vo.getAmount() == null || vo.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BizException("资金捐赠金额必须大于0");
            }
        } else {
            if (StrUtil.isBlank(vo.getGoodsName())) {
                throw new BizException("物资捐赠的物资名称不能为空");
            }
        }
        // 构建捐赠申请记录
        DonationApply donation = new DonationApply();
        donation.setUserId(userId);
        donation.setUserType(vo.getUserType());
        donation.setEnterpriseId(vo.getEnterpriseId());
        donation.setDonationType(vo.getDonationType());
        donation.setAmount(vo.getAmount());
        donation.setGoodsName(vo.getGoodsName());
        donation.setGoodsQuantity(vo.getGoodsQuantity());
        donation.setGoodsValue(vo.getGoodsValue());
        donation.setContactName(vo.getContactName());
        donation.setContactPhone(vo.getContactPhone());
        donation.setRemark(vo.getRemark());
        donation.setStatus(DonationStatusEnum.PENDING.getCode());
        // [新增 2026-08-03 17:40] 设置社区ID用于数据隔离
        donation.setCommunityId(vo.getCommunityId());
        donationApplyDao.insertDocument(donation);
        log.info("提交捐赠申请，donationId={}，userId={}，donationType={}", donation.getDonationId(), userId, vo.getDonationType());
        return donation;
    }

    /**
     * 分页查询捐赠申请
     * role=my时按当前用户筛选，支持按捐赠类型、状态筛选，按创建时间降序排列。
     *
     * @param userId 当前用户ID（role=my时生效）
     * @param vo     查询条件
     * @return 捐赠申请分页结果
     */
    public Page<DonationApply> queryDonationPage(Long userId, DonationQueryVO vo) {
        Pageable pageable = PageRequest.of(vo.getPageNumber(), vo.getPageSize(),
                Sort.by(Sort.Order.desc("createTime")));
        Criteria criteria = new Criteria();
        // [新增 2026-08-03 17:30] 社区数据隔离：按communityId过滤
        if (vo.getCommunityId() != null) {
            criteria.and("communityId").is(vo.getCommunityId());
        }
        // 视角筛选：my=我的捐赠
        if ("my".equalsIgnoreCase(vo.getRole())) {
            criteria.and("userId").is(userId);
        }
        // 捐赠类型筛选
        if (StrUtil.isNotBlank(vo.getDonationType())) {
            criteria.and("donationType").is(vo.getDonationType());
        }
        // 状态筛选
        if (StrUtil.isNotBlank(vo.getStatus())) {
            criteria.and("status").is(vo.getStatus());
        }
        return donationApplyDao.findDocumentPage(criteria, pageable);
    }

    /**
     * 审核捐赠申请
     * 校验申请是否存在且状态为待审核。
     * 审核通过且为企业捐赠（enterpriseId非空）时，更新企业累计捐赠统计：
     * 资金捐赠按amount计入，物资捐赠按goodsValue估值计入。
     *
     * @param vo 审核入参
     */
    public void auditDonation(DonationAuditVO vo) {
        // 查询申请是否存在
        DonationApply donation = donationApplyDao.findById(vo.getDonationId());
        if (donation == null) {
            throw new BizException("捐赠申请不存在");
        }
        // 校验当前状态必须为待审核
        if (!DonationStatusEnum.PENDING.getCode().equals(donation.getStatus())) {
            throw new BizException("该捐赠申请已审核，不可重复审核");
        }
        // 确定审核结果状态
        DonationStatusEnum newStatus = Boolean.TRUE.equals(vo.getApproved())
                ? DonationStatusEnum.APPROVED
                : DonationStatusEnum.REJECTED;
        donationApplyDao.updateOneDocument(
                Criteria.where("donationId").is(vo.getDonationId()),
                new Update()
                        .set("status", newStatus.getCode())
                        .set("auditRemark", vo.getAuditRemark())
                        .set("auditTime", new Date()));
        log.info("审核捐赠申请，donationId={}，approved={}，remark={}", vo.getDonationId(), vo.getApproved(), vo.getAuditRemark());

        // 审核通过且企业捐赠，更新企业累计捐赠统计
        if (Boolean.TRUE.equals(vo.getApproved()) && donation.getEnterpriseId() != null) {
            // 资金捐赠按amount计入，物资捐赠按goodsValue估值计入
            BigDecimal statAmount = DonationTypeEnum.MONEY.getCode().equals(donation.getDonationType())
                    ? (donation.getAmount() != null ? donation.getAmount() : BigDecimal.ZERO)
                    : (donation.getGoodsValue() != null ? donation.getGoodsValue() : BigDecimal.ZERO);
            charityEnterpriseService.updateDonationStats(donation.getEnterpriseId(), statAmount);
        }

        // [新增 2026-08-03] 通知申请人捐赠审核结果
        try {
            java.util.Map<String, String> msgData = new java.util.HashMap<>();
            msgData.put("thing1", "捐赠申请");
            msgData.put("phrase2", Boolean.TRUE.equals(vo.getApproved()) ? "已通过" : "已拒绝");
            msgData.put("thing3", vo.getAuditRemark() != null ? vo.getAuditRemark() : "");
            wechatMsgTemplateService.pushWechatSubscribeMsg(donation.getUserId(), "DONATION_RESULT", msgData, null);
        } catch (Exception e) {
            log.warn("捐赠审核结果通知发送失败，donationId={}", vo.getDonationId(), e);
        }
    }

    /**
     * 捐赠排行
     * 查询已通过或已完成的捐赠记录，按userId分组对amount求和降序，返回排名前列用户的捐赠记录。
     * 使用groupSumDescByField进行分组聚合，再查询对应用户的捐赠明细。
     *
     * @param limit       返回数量上限
     * @param communityId 社区ID（用于数据隔离，null则不过滤）
     * @return 排名前列的捐赠申请记录
     */
    // [变更 2026-08-03 18:00] 增加communityId参数用于社区数据隔离
    public List<DonationApply> getDonationRanking(int limit, Long communityId) {
        // 查询已通过或已完成的捐赠，按userId分组对amount求和降序
        Criteria criteria = Criteria.where("status").in(
                DonationStatusEnum.APPROVED.getCode(),
                DonationStatusEnum.COMPLETED.getCode());
        // [新增 2026-08-03 18:00] 社区数据隔离：按communityId过滤
        if (communityId != null) {
            criteria.and("communityId").is(communityId);
        }
        List<HashMap> ranking = donationApplyDao.groupSumDescByField(criteria, "userId", "amount", limit);
        if (ranking == null || ranking.isEmpty()) {
            return Collections.emptyList();
        }
        // 提取排名前列的userId
        List<Long> topUserIds = new ArrayList<>();
        for (HashMap map : ranking) {
            Object idObj = map.get("_id");
            if (idObj instanceof Number) {
                topUserIds.add(((Number) idObj).longValue());
            }
        }
        if (topUserIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 查询这些用户的已通过/已完成捐赠记录（同样按社区过滤）
        Criteria detailCriteria = Criteria.where("userId").in(topUserIds)
                .and("status").in(DonationStatusEnum.APPROVED.getCode(), DonationStatusEnum.COMPLETED.getCode());
        if (communityId != null) {
            detailCriteria.and("communityId").is(communityId);
        }
        return donationApplyDao.findDocumentList(detailCriteria);
    }
}
