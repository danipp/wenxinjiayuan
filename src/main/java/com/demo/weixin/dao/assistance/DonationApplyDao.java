package com.demo.weixin.dao.assistance;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.assistance.DonationApply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 捐赠申请数据访问层
 */
@Repository
public class DonationApplyDao extends BaseDAO<DonationApply> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public DonationApplyDao() {
        ID = "donationId";
        clazz = DonationApply.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
