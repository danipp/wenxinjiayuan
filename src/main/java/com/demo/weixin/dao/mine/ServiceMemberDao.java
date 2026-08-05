package com.demo.weixin.dao.mine;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.mine.ServiceMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 服务对象数据访问层
 */
@Repository
public class ServiceMemberDao extends BaseDAO<ServiceMember> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public ServiceMemberDao() {
        ID = "memberId";
        clazz = ServiceMember.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
