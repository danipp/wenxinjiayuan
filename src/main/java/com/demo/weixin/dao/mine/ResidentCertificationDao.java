package com.demo.weixin.dao.mine;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.mine.ResidentCertification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 居民认证数据访问层
 */
@Repository
public class ResidentCertificationDao extends BaseDAO<ResidentCertification> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public ResidentCertificationDao() {
        ID = "certificationId";
        clazz = ResidentCertification.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
