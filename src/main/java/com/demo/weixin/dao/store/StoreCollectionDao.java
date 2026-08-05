package com.demo.weixin.dao.store;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.store.StoreCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 商城收藏数据访问层
 */
@Repository
public class StoreCollectionDao extends BaseDAO<StoreCollection> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public StoreCollectionDao() {
        ID = "collectionId";
        clazz = StoreCollection.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
