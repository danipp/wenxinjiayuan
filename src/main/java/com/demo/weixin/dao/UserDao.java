package com.demo.weixin.dao;

import com.demo.weixin.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends BaseDAO<User> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public UserDao() {
        ID = "userId";
        clazz = User.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
