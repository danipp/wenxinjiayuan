package com.demo.weixin.dao.assistance;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.assistance.AssistanceApply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 帮扶申请数据访问层
 */
@Repository
public class AssistanceApplyDao extends BaseDAO<AssistanceApply> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public AssistanceApplyDao() {
        ID = "applyId";
        clazz = AssistanceApply.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
