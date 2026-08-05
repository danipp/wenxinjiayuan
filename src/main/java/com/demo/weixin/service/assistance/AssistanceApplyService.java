package com.demo.weixin.service.assistance;

import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.assistance.AssistanceApplyDao;
import com.demo.weixin.entity.assistance.AssistanceApply;
import com.demo.weixin.enums.assistance.AssistanceStatusEnum;
import com.demo.weixin.vo.assistance.AssistanceApplyVO;
import com.demo.weixin.vo.assistance.AssistanceAuditVO;
import com.demo.weixin.vo.assistance.AssistanceQueryVO;
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
 * 帮扶申请服务
 * 提供帮扶申请提交、分页查询、审核功能。
 * 困难用户提交帮扶申请后由管理员审核，审核通过/拒绝后不可重复审核。
 */
@Service
@Slf4j
public class AssistanceApplyService {

    @Autowired
    private AssistanceApplyDao assistanceApplyDao;

    @Autowired
    private com.demo.weixin.service.WechatMsgTemplateService wechatMsgTemplateService;

    /**
     * 提交帮扶申请
     * 设置状态为待审核，userId从参数获取。
     *
     * @param userId 申请用户ID
     * @param vo     帮扶申请入参
     * @return 创建后的帮扶申请记录
     */
    public AssistanceApply submitApply(Long userId, AssistanceApplyVO vo) {
        AssistanceApply apply = new AssistanceApply();
        apply.setUserId(userId);
        apply.setApplicantName(vo.getApplicantName());
        apply.setApplicantPhone(vo.getApplicantPhone());
        apply.setIdCard(vo.getIdCard());
        apply.setAddress(vo.getAddress());
        apply.setFamilySituation(vo.getFamilySituation());
        apply.setAssistanceType(vo.getAssistanceType());
        apply.setDifficultyDesc(vo.getDifficultyDesc());
        apply.setDesiredHelp(vo.getDesiredHelp());
        apply.setRemark(vo.getRemark());
        apply.setStatus(AssistanceStatusEnum.PENDING.getCode());
        // [新增 2026-08-03 17:40] 设置社区ID用于数据隔离
        apply.setCommunityId(vo.getCommunityId());
        assistanceApplyDao.insertDocument(apply);
        log.info("提交帮扶申请，applyId={}，userId={}，assistanceType={}", apply.getApplyId(), userId, vo.getAssistanceType());
        return apply;
    }

    /**
     * 分页查询帮扶申请
     * role=my时按当前用户筛选，支持按状态、帮扶类型筛选，按创建时间降序排列。
     *
     * @param userId 当前用户ID（role=my时生效）
     * @param vo     查询条件
     * @return 帮扶申请分页结果
     */
    public Page<AssistanceApply> queryApplyPage(Long userId, AssistanceQueryVO vo) {
        Pageable pageable = PageRequest.of(vo.getPageNumber(), vo.getPageSize(),
                Sort.by(Sort.Order.desc("createTime")));
        Criteria criteria = new Criteria();
        // [新增 2026-08-03 17:30] 社区数据隔离：按communityId过滤
        if (vo.getCommunityId() != null) {
            criteria.and("communityId").is(vo.getCommunityId());
        }
        // 视角筛选：my=我的申请
        if ("my".equalsIgnoreCase(vo.getRole())) {
            criteria.and("userId").is(userId);
        }
        // 状态筛选
        if (StrUtil.isNotBlank(vo.getStatus())) {
            criteria.and("status").is(vo.getStatus());
        }
        // 帮扶类型筛选
        if (StrUtil.isNotBlank(vo.getAssistanceType())) {
            criteria.and("assistanceType").is(vo.getAssistanceType());
        }
        return assistanceApplyDao.findDocumentPage(criteria, pageable);
    }

    /**
     * 审核帮扶申请
     * 校验申请是否存在且状态为待审核，approved=true通过，false拒绝。
     *
     * @param vo 审核入参
     */
    public void auditApply(AssistanceAuditVO vo) {
        // 查询申请是否存在
        AssistanceApply apply = assistanceApplyDao.findById(vo.getApplyId());
        if (apply == null) {
            throw new BizException("帮扶申请不存在");
        }
        // 校验当前状态必须为待审核
        if (!AssistanceStatusEnum.PENDING.getCode().equals(apply.getStatus())) {
            throw new BizException("该帮扶申请已审核，不可重复审核");
        }
        // 确定审核结果状态
        AssistanceStatusEnum newStatus = Boolean.TRUE.equals(vo.getApproved())
                ? AssistanceStatusEnum.APPROVED
                : AssistanceStatusEnum.REJECTED;
        assistanceApplyDao.updateOneDocument(
                Criteria.where("applyId").is(vo.getApplyId()),
                new Update()
                        .set("status", newStatus.getCode())
                        .set("auditRemark", vo.getAuditRemark())
                        .set("auditTime", new Date()));
        log.info("审核帮扶申请，applyId={}，approved={}，remark={}", vo.getApplyId(), vo.getApproved(), vo.getAuditRemark());

        // [新增 2026-08-03] 通知申请人帮扶审核结果
        try {
            java.util.Map<String, String> msgData = new java.util.HashMap<>();
            msgData.put("thing1", "帮扶申请");
            msgData.put("phrase2", Boolean.TRUE.equals(vo.getApproved()) ? "已通过" : "已拒绝");
            msgData.put("thing3", vo.getAuditRemark() != null ? vo.getAuditRemark() : "");
            wechatMsgTemplateService.pushWechatSubscribeMsg(apply.getUserId(), "ASSISTANCE_RESULT", msgData, null);
        } catch (Exception e) {
            log.warn("帮扶审核结果通知发送失败，applyId={}", vo.getApplyId(), e);
        }
    }
}
