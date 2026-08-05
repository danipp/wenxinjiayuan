package com.demo.weixin.dao.store;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.store.StoreOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 商城订单数据访问层
 */
@Repository
public class StoreOrderDao extends BaseDAO<StoreOrder> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public StoreOrderDao() {
        ID = "orderId";
        clazz = StoreOrder.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
