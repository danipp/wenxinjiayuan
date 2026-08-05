package com.demo.weixin.dao.special;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.special.ShopCouponRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 优惠券领券记录数据访问层
 */
@Repository
public class ShopCouponRecordDao extends BaseDAO<ShopCouponRecord> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public ShopCouponRecordDao() {
        ID = "recordId";
        clazz = ShopCouponRecord.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
