package com.demo.weixin.dao.activity;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.activity.ActivityPhoto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 活动照片数据访问层
 */
@Repository
public class ActivityPhotoDao extends BaseDAO<ActivityPhoto> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public ActivityPhotoDao() {
        ID = "photoId";
        clazz = ActivityPhoto.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
