package com.demo.weixin.service.mine;

import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.mine.EmergencyContactDao;
import com.demo.weixin.entity.mine.EmergencyContact;
import com.demo.weixin.vo.mine.EmergencyContactVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 紧急联系人服务
 * 提供联系人列表查询、添加/编辑、删除功能。
 */
@Service
@Slf4j
public class EmergencyContactService {

    @Autowired
    private EmergencyContactDao emergencyContactDao;

    /**
     * 获取用户的紧急联系人列表
     *
     * @param userId 用户ID
     * @return 联系人列表
     */
    public List<EmergencyContact> getContactList(Long userId) {
        return emergencyContactDao.findDocumentList(
                Criteria.where("userId").is(userId),
                Sort.Order.desc("createTime"));
    }

    /**
     * 添加或编辑紧急联系人
     * contactId 为空时新增，非空时更新已有记录。
     *
     * @param userId 用户ID
     * @param vo     联系人入参
     * @return 保存后的联系人
     */
    public EmergencyContact saveOrUpdateContact(Long userId, EmergencyContactVO vo) {
        if (StrUtil.isBlank(vo.getName())) {
            throw new BizException("联系人姓名不能为空");
        }
        if (StrUtil.isBlank(vo.getPhone())) {
            throw new BizException("联系人电话不能为空");
        }
        if (vo.getContactId() != null) {
            // 编辑：校验记录存在且属于当前用户
            EmergencyContact existing = emergencyContactDao.findById(vo.getContactId());
            if (existing == null) {
                throw new BizException("联系人不存在");
            }
            if (!existing.getUserId().equals(userId)) {
                throw new BizException("无权操作他人联系人");
            }
            emergencyContactDao.updateOneDocument(
                    Criteria.where("contactId").is(vo.getContactId()),
                    new Update()
                            .set("name", vo.getName())
                            .set("phone", vo.getPhone())
                            .set("relation", vo.getRelation()));
            log.info("编辑紧急联系人，contactId={}，userId={}", vo.getContactId(), userId);
            return emergencyContactDao.findById(vo.getContactId());
        }
        // 新增前数量限制：每个用户最多10个紧急联系人
        long existCount = emergencyContactDao.count(Criteria.where("userId").is(userId));
        if (existCount >= 10) {
            throw new BizException("紧急联系人最多10个");
        }
        // 新增
        EmergencyContact contact = new EmergencyContact();
        contact.setUserId(userId);
        contact.setName(vo.getName());
        contact.setPhone(vo.getPhone());
        contact.setRelation(vo.getRelation());
        emergencyContactDao.insertDocument(contact);
        log.info("新增紧急联系人，contactId={}，userId={}", contact.getContactId(), userId);
        return contact;
    }

    /**
     * 删除紧急联系人
     *
     * @param userId    用户ID
     * @param contactId 联系人ID
     */
    public void deleteContact(Long userId, Long contactId) {
        EmergencyContact existing = emergencyContactDao.findById(contactId);
        if (existing == null) {
            throw new BizException("联系人不存在");
        }
        if (!existing.getUserId().equals(userId)) {
            throw new BizException("无权删除他人联系人");
        }
        emergencyContactDao.deleteDocument(contactId);
        log.info("删除紧急联系人，contactId={}，userId={}", contactId, userId);
    }
}
