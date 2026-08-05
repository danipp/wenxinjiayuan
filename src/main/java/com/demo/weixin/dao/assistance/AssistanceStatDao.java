package com.demo.weixin.dao.assistance;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.assistance.AssistanceStat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 帮扶统计数据访问层
 */
@Repository
public class AssistanceStatDao extends BaseDAO<AssistanceStat> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public AssistanceStatDao() {
        ID = "statId";
        clazz = AssistanceStat.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
