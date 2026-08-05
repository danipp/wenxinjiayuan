package com.demo.weixin.dao.activity;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.activity.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 活动数据访问层
 */
@Repository
public class ActivityDao extends BaseDAO<Activity> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public ActivityDao() {
        ID = "activityId";
        clazz = Activity.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
