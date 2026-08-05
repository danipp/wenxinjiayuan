package com.demo.weixin.dao.assistance;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.assistance.CharityEnterprise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 慈善企业数据访问层
 */
@Repository
public class CharityEnterpriseDao extends BaseDAO<CharityEnterprise> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public CharityEnterpriseDao() {
        ID = "enterpriseId";
        clazz = CharityEnterprise.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
