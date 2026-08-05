package com.demo.weixin.dao.store;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.store.StoreOrderComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 商城订单评价数据访问层
 */
@Repository
public class StoreOrderCommentDao extends BaseDAO<StoreOrderComment> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public StoreOrderCommentDao() {
        ID = "commentId";
        clazz = StoreOrderComment.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
