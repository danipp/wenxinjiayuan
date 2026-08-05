package com.demo.weixin.dao;

import com.demo.weixin.entity.wx.WechatMsgTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WechatMsgTemplateDao extends BaseDAO<WechatMsgTemplate> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public WechatMsgTemplateDao() {
        ID = "id";
        clazz = WechatMsgTemplate.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
