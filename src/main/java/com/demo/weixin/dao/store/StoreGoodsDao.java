package com.demo.weixin.dao.store;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.store.StoreGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 商城商品数据访问层
 */
@Repository
public class StoreGoodsDao extends BaseDAO<StoreGoods> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public StoreGoodsDao() {
        ID = "goodsId";
        clazz = StoreGoods.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
