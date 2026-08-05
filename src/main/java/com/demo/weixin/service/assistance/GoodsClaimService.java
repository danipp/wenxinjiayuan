package com.demo.weixin.service.assistance;

import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.assistance.GoodsClaimDao;
import com.demo.weixin.entity.assistance.GoodsClaim;
import com.demo.weixin.entity.store.StoreGoods;
import com.demo.weixin.enums.assistance.ClaimStatusEnum;
import com.demo.weixin.service.store.StoreGoodsService;
import com.demo.weixin.vo.assistance.ClaimAuditVO;
import com.demo.weixin.vo.assistance.ClaimQueryVO;
import com.demo.weixin.vo.assistance.GoodsClaimVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 物资申领服务
 * 提供物资申领提交、分页查询、审核、发放功能。
 * 申领时从商城商品服务获取商品信息并冗余存储标题和图片。
 */
@Service
@Slf4j
public class GoodsClaimService {

    @Autowired
    private GoodsClaimDao goodsClaimDao;

    @Autowired
    private StoreGoodsService storeGoodsService;

    @Autowired
    private com.demo.weixin.service.WechatMsgTemplateService wechatMsgTemplateService;

    /**
     * 提交物资申领
     * 从商城商品服务获取商品信息，冗余存储goodsTitle和goodsImage，设置状态为待审核。
     *
     * @param userId 申领用户ID
     * @param vo     物资申领入参
     * @return 创建后的物资申领记录
     */
    public GoodsClaim submitClaim(Long userId, GoodsClaimVO vo) {
        // 查询商品信息，冗余存储标题和图片
        StoreGoods goods = storeGoodsService.getGoodsDetail(vo.getGoodsId());
        GoodsClaim claim = new GoodsClaim();
        claim.setUserId(userId);
        claim.setGoodsId(vo.getGoodsId());
        claim.setGoodsTitle(goods.getTitle());
        claim.setGoodsImage(goods.getCoverImage());
        claim.setClaimCount(vo.getClaimCount());
        claim.setClaimReason(vo.getClaimReason());
        claim.setContactName(vo.getContactName());
        claim.setContactPhone(vo.getContactPhone());
        claim.setAddress(vo.getAddress());
        claim.setStatus(ClaimStatusEnum.PENDING.getCode());
        // [新增 2026-08-03 17:40] 从商品继承社区ID用于数据隔离
        claim.setCommunityId(goods.getCommunityId());
        claim.setCommunityName(goods.getCommunityName());
        goodsClaimDao.insertDocument(claim);
        log.info("提交物资申领，claimId={}，userId={}，goodsId={}", claim.getClaimId(), userId, vo.getGoodsId());
        return claim;
    }

    /**
     * 分页查询物资申领
     * role=my时按当前用户筛选，支持按商品ID、状态筛选，按创建时间降序排列。
     *
     * @param userId 当前用户ID（role=my时生效）
     * @param vo     查询条件
     * @return 物资申领分页结果
     */
    public Page<GoodsClaim> queryClaimPage(Long userId, ClaimQueryVO vo) {
        Pageable pageable = PageRequest.of(vo.getPageNumber(), vo.getPageSize(),
                Sort.by(Sort.Order.desc("createTime")));
        Criteria criteria = new Criteria();
        // [新增 2026-08-03 17:30] 社区数据隔离：按communityId过滤
        if (vo.getCommunityId() != null) {
            criteria.and("communityId").is(vo.getCommunityId());
        }
        // 视角筛选：my=我的申领
        if ("my".equalsIgnoreCase(vo.getRole())) {
            criteria.and("userId").is(userId);
        }
        // 商品ID筛选
        if (vo.getGoodsId() != null) {
            criteria.and("goodsId").is(vo.getGoodsId());
        }
        // 状态筛选
        if (StrUtil.isNotBlank(vo.getStatus())) {
            criteria.and("status").is(vo.getStatus());
        }
        return goodsClaimDao.findDocumentPage(criteria, pageable);
    }

    /**
     * 审核物资申领
     * 校验申领是否存在且状态为待审核，approved=true通过，false拒绝。
     *
     * @param vo 审核入参
     */
    public void auditClaim(ClaimAuditVO vo) {
        // 查询申领是否存在
        GoodsClaim claim = goodsClaimDao.findById(vo.getClaimId());
        if (claim == null) {
            throw new BizException("物资申领不存在");
        }
        // 校验当前状态必须为待审核
        if (!ClaimStatusEnum.PENDING.getCode().equals(claim.getStatus())) {
            throw new BizException("该物资申领已审核，不可重复审核");
        }
        // 确定审核结果状态
        ClaimStatusEnum newStatus = Boolean.TRUE.equals(vo.getApproved())
                ? ClaimStatusEnum.APPROVED
                : ClaimStatusEnum.REJECTED;
        goodsClaimDao.updateOneDocument(
                Criteria.where("claimId").is(vo.getClaimId()),
                new Update()
                        .set("status", newStatus.getCode())
                        .set("auditRemark", vo.getAuditRemark())
                        .set("auditTime", new Date()));
        log.info("审核物资申领，claimId={}，approved={}，remark={}", vo.getClaimId(), vo.getApproved(), vo.getAuditRemark());

        // [新增 2026-08-03] 通知申请人物资申领审核结果
        try {
            java.util.Map<String, String> msgData = new java.util.HashMap<>();
            msgData.put("thing1", "物资申领");
            msgData.put("phrase2", Boolean.TRUE.equals(vo.getApproved()) ? "已通过" : "已拒绝");
            msgData.put("thing3", vo.getAuditRemark() != null ? vo.getAuditRemark() : "");
            wechatMsgTemplateService.pushWechatSubscribeMsg(claim.getUserId(), "CLAIM_RESULT", msgData, null);
        } catch (Exception e) {
            log.warn("物资申领审核结果通知发送失败，claimId={}", vo.getClaimId(), e);
        }
    }

    /**
     * 发放物资
     * 校验当前状态必须为已通过（APPROVED），发放后状态流转为已发放（DISTRIBUTED）。
     *
     * @param claimId 申领ID
     */
    public void distributeClaim(Long claimId) {
        // 查询申领是否存在
        GoodsClaim claim = goodsClaimDao.findById(claimId);
        if (claim == null) {
            throw new BizException("物资申领不存在");
        }
        // 校验当前状态必须为已通过
        if (!ClaimStatusEnum.APPROVED.getCode().equals(claim.getStatus())) {
            throw new BizException("当前申领状态不可发放，仅审核通过的申领可发放");
        }
        goodsClaimDao.updateOneDocument(
                Criteria.where("claimId").is(claimId),
                new Update().set("status", ClaimStatusEnum.DISTRIBUTED.getCode()));
        log.info("发放物资，claimId={}", claimId);
    }
}
