package com.demo.weixin.dao.activity;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.activity.ActivitySignup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 活动报名数据访问层
 */
@Repository
public class ActivitySignupDao extends BaseDAO<ActivitySignup> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public ActivitySignupDao() {
        ID = "signupId";
        clazz = ActivitySignup.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
