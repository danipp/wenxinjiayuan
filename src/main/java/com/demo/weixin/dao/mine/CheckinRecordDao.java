package com.demo.weixin.dao.mine;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.mine.CheckinRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 打卡记录数据访问层
 */
@Repository
public class CheckinRecordDao extends BaseDAO<CheckinRecord> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public CheckinRecordDao() {
        ID = "recordId";
        clazz = CheckinRecord.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
