package com.demo.weixin.dao.notice;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.notice.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 消息通知数据访问层
 * [新增 2026-08-03 19:30]
 */
@Repository
public class NoticeDao extends BaseDAO<Notice> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public NoticeDao() {
        ID = "noticeId";
        clazz = Notice.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
