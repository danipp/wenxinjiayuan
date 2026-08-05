package com.demo.weixin.service.mine;

import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.mine.ServiceMemberDao;
import com.demo.weixin.entity.mine.ServiceMember;
import com.demo.weixin.vo.mine.ServiceMemberVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务对象服务
 * 提供服务对象列表查询、添加/编辑、删除功能。
 * 服务对象用于发布服务需求时快速选择被服务人信息。
 */
@Service
@Slf4j
public class ServiceMemberService {

    @Autowired
    private ServiceMemberDao serviceMemberDao;

    /**
     * 获取用户的服务对象列表
     *
     * @param userId 用户ID
     * @return 服务对象列表
     */
    public List<ServiceMember> getMemberList(Long userId) {
        return serviceMemberDao.findDocumentList(
                Criteria.where("userId").is(userId),
                Sort.Order.desc("createTime"));
    }

    /**
     * 添加或编辑服务对象
     * memberId 为空时新增，非空时更新已有记录。
     *
     * @param userId 用户ID
     * @param vo     服务对象入参
     * @return 保存后的服务对象
     */
    public ServiceMember saveOrUpdateMember(Long userId, ServiceMemberVO vo) {
        if (StrUtil.isBlank(vo.getName())) {
            throw new BizException("姓名不能为空");
        }
        if (StrUtil.isBlank(vo.getPhone())) {
            throw new BizException("手机号不能为空");
        }
        if (vo.getMemberId() != null) {
            // 编辑：校验记录存在且属于当前用户
            ServiceMember existing = serviceMemberDao.findById(vo.getMemberId());
            if (existing == null) {
                throw new BizException("服务对象不存在");
            }
            if (!existing.getUserId().equals(userId)) {
                throw new BizException("无权操作他人服务对象");
            }
            serviceMemberDao.updateOneDocument(
                    Criteria.where("memberId").is(vo.getMemberId()),
                    new Update()
                            .set("name", vo.getName())
                            .set("phone", vo.getPhone())
                            .set("address", vo.getAddress())
                            .set("detailAddress", vo.getDetailAddress())
                            .set("remark", vo.getRemark()));
            log.info("编辑服务对象，memberId={}，userId={}", vo.getMemberId(), userId);
            return serviceMemberDao.findById(vo.getMemberId());
        }
        // 新增前数量限制：每个用户最多20个服务对象
        long existCount = serviceMemberDao.count(Criteria.where("userId").is(userId));
        if (existCount >= 20) {
            throw new BizException("服务对象最多20个");
        }
        // 新增
        ServiceMember member = new ServiceMember();
        member.setUserId(userId);
        member.setName(vo.getName());
        member.setPhone(vo.getPhone());
        member.setAddress(vo.getAddress());
        member.setDetailAddress(vo.getDetailAddress());
        member.setRemark(vo.getRemark());
        serviceMemberDao.insertDocument(member);
        log.info("新增服务对象，memberId={}，userId={}", member.getMemberId(), userId);
        return member;
    }

    /**
     * 删除服务对象
     *
     * @param userId   用户ID
     * @param memberId 服务对象ID
     */
    public void deleteMember(Long userId, Long memberId) {
        ServiceMember existing = serviceMemberDao.findById(memberId);
        if (existing == null) {
            throw new BizException("服务对象不存在");
        }
        if (!existing.getUserId().equals(userId)) {
            throw new BizException("无权删除他人服务对象");
        }
        serviceMemberDao.deleteDocument(memberId);
        log.info("删除服务对象，memberId={}，userId={}", memberId, userId);
    }
}
