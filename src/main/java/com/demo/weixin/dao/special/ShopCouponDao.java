package com.demo.weixin.dao.special;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.special.ShopCoupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 店铺优惠券数据访问层
 */
@Repository
public class ShopCouponDao extends BaseDAO<ShopCoupon> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public ShopCouponDao() {
        ID = "couponId";
        clazz = ShopCoupon.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
