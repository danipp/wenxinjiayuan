package com.demo.weixin.dao.ad;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.ad.Ad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 广告位数据访问层
 */
@Repository
public class AdDao extends BaseDAO<Ad> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public AdDao() {
        ID = "adId";
        clazz = Ad.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
