package com.demo.weixin.dao;

import com.demo.weixin.entity.SystemAuditLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SystemAuditLogDao extends BaseDAO<SystemAuditLog> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public SystemAuditLogDao() {
        ID = "id";
        clazz = SystemAuditLog.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
