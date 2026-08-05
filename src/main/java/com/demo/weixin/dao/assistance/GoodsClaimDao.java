package com.demo.weixin.dao.assistance;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.assistance.GoodsClaim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 物资认领数据访问层
 */
@Repository
public class GoodsClaimDao extends BaseDAO<GoodsClaim> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public GoodsClaimDao() {
        ID = "claimId";
        clazz = GoodsClaim.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
