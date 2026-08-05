package com.demo.weixin.dao.store;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.store.StoreShop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 商城店铺数据访问层
 */
@Repository
public class StoreShopDao extends BaseDAO<StoreShop> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public StoreShopDao() {
        ID = "shopId";
        clazz = StoreShop.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
