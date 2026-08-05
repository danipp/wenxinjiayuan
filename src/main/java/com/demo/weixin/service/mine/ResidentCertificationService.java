package com.demo.weixin.service.mine;

import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.mine.ResidentCertificationDao;
import com.demo.weixin.entity.mine.ResidentCertification;
import com.demo.weixin.enums.mine.CertificationStatusEnum;
import com.demo.weixin.vo.mine.CertificationAuditVO;
import com.demo.weixin.vo.mine.ResidentCertificationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 居民认证服务
 * 提供认证提交、认证状态查询、管理员审核功能。
 * 每个用户仅维护一条认证记录，重复提交时更新原有记录并重置状态为待审核。
 */
@Service
@Slf4j
public class ResidentCertificationService {

    @Autowired
    private ResidentCertificationDao residentCertificationDao;

    @Autowired
    private com.demo.weixin.service.WechatMsgTemplateService wechatMsgTemplateService;

    /**
     * 提交居民认证
     * 若用户已有认证记录则更新（重新提交审核），否则新建。
     *
     * @param userId 用户ID
     * @param vo     认证入参
     * @return 认证记录
     */
    public ResidentCertification submitCertification(Long userId, ResidentCertificationVO vo) {
        // 参数校验
        if (StrUtil.isBlank(vo.getPhone())) {
            throw new BizException("手机号不能为空");
        }
        if (StrUtil.isBlank(vo.getCommunityName())) {
            throw new BizException("社区名称不能为空");
        }
        // 查询是否已有认证记录
        ResidentCertification existing = residentCertificationDao.findOne(
                Criteria.where("userId").is(userId));
        if (existing != null) {
            // 已通过认证的不允许重新提交重置，避免覆盖已认证状态；仅待审核/已拒绝可重新提交
            if (CertificationStatusEnum.APPROVED.getCode().equals(existing.getStatus())) {
                throw new BizException("您已通过认证，如需修改请联系管理员");
            }
            // 已有记录：更新信息并重置为待审核
            residentCertificationDao.updateOneDocument(
                    Criteria.where("certificationId").is(existing.getCertificationId()),
                    new Update()
                            .set("phone", vo.getPhone())
                            .set("communityName", vo.getCommunityName())
                            .set("realName", vo.getRealName())
                            .set("idCard", vo.getIdCard())
                            .set("address", vo.getAddress())
                            .set("status", CertificationStatusEnum.PENDING.getCode())
                            .set("auditRemark", null)
                            .set("auditTime", null));
            log.info("用户重新提交认证，certificationId={}，userId={}", existing.getCertificationId(), userId);
            return residentCertificationDao.findById(existing.getCertificationId());
        }
        // 新建认证记录
        ResidentCertification certification = new ResidentCertification();
        certification.setUserId(userId);
        certification.setPhone(vo.getPhone());
        certification.setCommunityName(vo.getCommunityName());
        certification.setRealName(vo.getRealName());
        certification.setIdCard(vo.getIdCard());
        certification.setAddress(vo.getAddress());
        certification.setStatus(CertificationStatusEnum.PENDING.getCode());
        residentCertificationDao.insertDocument(certification);
        log.info("用户提交认证，certificationId={}，userId={}", certification.getCertificationId(), userId);
        return certification;
    }

    /**
     * 查询当前用户的认证状态
     *
     * @param userId 用户ID
     * @return 认证记录（无记录时返回null）
     */
    public ResidentCertification getMyCertification(Long userId) {
        return residentCertificationDao.findOne(Criteria.where("userId").is(userId));
    }

    /**
     * 管理员审核认证
     *
     * @param vo 审核入参
     */
    public void auditCertification(CertificationAuditVO vo) {
        if (vo.getCertificationId() == null) {
            throw new BizException("认证记录ID不能为空");
        }
        CertificationStatusEnum statusEnum = CertificationStatusEnum.getByCode(vo.getStatus());
        if (statusEnum == null || (statusEnum != CertificationStatusEnum.APPROVED && statusEnum != CertificationStatusEnum.REJECTED)) {
            throw new BizException("无效的审核结果，仅支持 APPROVED 或 REJECTED");
        }
        ResidentCertification existing = residentCertificationDao.findById(vo.getCertificationId());
        if (existing == null) {
            throw new BizException("认证记录不存在");
        }
        if (!CertificationStatusEnum.PENDING.getCode().equals(existing.getStatus())) {
            throw new BizException("该认证记录已审核，不可重复审核");
        }
        residentCertificationDao.updateOneDocument(
                Criteria.where("certificationId").is(vo.getCertificationId()),
                new Update()
                        .set("status", statusEnum.getCode())
                        .set("auditRemark", vo.getAuditRemark())
                        .set("auditTime", new Date()));
        log.info("审核认证，certificationId={}，status={}，remark={}", vo.getCertificationId(), statusEnum.getCode(), vo.getAuditRemark());

        // [新增 2026-08-03] 通知申请人认证审核结果
        try {
            java.util.Map<String, String> msgData = new java.util.HashMap<>();
            msgData.put("thing1", "居民认证");
            msgData.put("phrase2", CertificationStatusEnum.APPROVED.equals(statusEnum) ? "已通过" : "已拒绝");
            msgData.put("thing3", vo.getAuditRemark() != null ? vo.getAuditRemark() : "");
            wechatMsgTemplateService.pushWechatSubscribeMsg(existing.getUserId(), "CERTIFICATION_RESULT", msgData, null);
        } catch (Exception e) {
            log.warn("认证审核结果通知发送失败，userId={}", existing.getUserId(), e);
        }
    }
}
