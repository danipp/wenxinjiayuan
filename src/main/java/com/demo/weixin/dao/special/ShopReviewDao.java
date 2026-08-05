package com.demo.weixin.dao.special;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.special.ShopReview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 店铺评价数据访问层
 */
@Repository
public class ShopReviewDao extends BaseDAO<ShopReview> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public ShopReviewDao() {
        ID = "reviewId";
        clazz = ShopReview.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
