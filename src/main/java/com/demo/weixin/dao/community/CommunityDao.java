package com.demo.weixin.dao.community;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.community.Community;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 社区数据访问层
 */
@Repository
public class CommunityDao extends BaseDAO<Community> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public CommunityDao() {
        ID = "communityId";
        clazz = Community.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
