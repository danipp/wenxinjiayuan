package com.demo.weixin.dao;

import com.demo.weixin.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EventDao extends BaseDAO<Event> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public EventDao() {
        ID = "eventId";
        clazz = Event.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
