package com.demo.weixin.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.demo.weixin.entity.Admin;

@Repository
public class AdminDao extends BaseDAO<Admin> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public AdminDao() {
        ID = "adminId";
        clazz = Admin.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
