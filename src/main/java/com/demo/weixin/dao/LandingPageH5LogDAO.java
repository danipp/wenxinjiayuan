package com.demo.weixin.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.demo.weixin.entity.LandingPageH5Log;

@Repository
public class LandingPageH5LogDAO extends BaseDAO<LandingPageH5Log> {

	@Autowired
	private MongoTemplate mongoTemplate;

	public LandingPageH5LogDAO() {
		ID = "id";
		clazz = LandingPageH5Log.class;
	}

	@Override
	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}
}
