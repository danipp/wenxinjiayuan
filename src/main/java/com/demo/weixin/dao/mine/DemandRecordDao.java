package com.demo.weixin.dao.mine;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.mine.DemandRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 需求/帮忙记录数据访问层
 */
@Repository
public class DemandRecordDao extends BaseDAO<DemandRecord> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public DemandRecordDao() {
        ID = "demandId";
        clazz = DemandRecord.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
