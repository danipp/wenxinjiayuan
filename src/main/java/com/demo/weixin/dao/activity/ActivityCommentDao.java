package com.demo.weixin.dao.activity;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.activity.ActivityComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 活动评价数据访问层
 */
@Repository
public class ActivityCommentDao extends BaseDAO<ActivityComment> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public ActivityCommentDao() {
        ID = "commentId";
        clazz = ActivityComment.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
