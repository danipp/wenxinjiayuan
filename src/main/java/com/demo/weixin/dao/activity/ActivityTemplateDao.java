package com.demo.weixin.dao.activity;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.activity.ActivityTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 活动模板数据访问层
 */
@Repository
public class ActivityTemplateDao extends BaseDAO<ActivityTemplate> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public ActivityTemplateDao() {
        ID = "templateId";
        clazz = ActivityTemplate.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
