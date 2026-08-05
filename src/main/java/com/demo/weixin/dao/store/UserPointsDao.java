package com.demo.weixin.dao.store;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.store.UserPoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 用户积分数据访问层
 */
@Repository
public class UserPointsDao extends BaseDAO<UserPoints> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public UserPointsDao() {
        ID = "userPointsId";
        clazz = UserPoints.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
