package com.demo.weixin.dao.mine;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.mine.EmergencyContact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 紧急联系人数据访问层
 */
@Repository
public class EmergencyContactDao extends BaseDAO<EmergencyContact> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public EmergencyContactDao() {
        ID = "contactId";
        clazz = EmergencyContact.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
