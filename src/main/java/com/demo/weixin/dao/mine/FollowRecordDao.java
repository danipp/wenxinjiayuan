package com.demo.weixin.dao.mine;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.mine.FollowRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 关注记录数据访问层
 */
@Repository
public class FollowRecordDao extends BaseDAO<FollowRecord> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public FollowRecordDao() {
        ID = "followId";
        clazz = FollowRecord.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
