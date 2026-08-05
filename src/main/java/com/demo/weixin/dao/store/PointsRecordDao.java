package com.demo.weixin.dao.store;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.store.PointsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 积分流水记录数据访问层
 * [新增 2026-08-03 19:10]
 */
@Repository
public class PointsRecordDao extends BaseDAO<PointsRecord> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public PointsRecordDao() {
        ID = "recordId";
        clazz = PointsRecord.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
