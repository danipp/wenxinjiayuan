package com.demo.weixin.dao.special;

import com.demo.weixin.dao.BaseDAO;
import com.demo.weixin.entity.special.SpecialCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 社区特惠分类数据访问层
 */
@Repository
public class SpecialCategoryDao extends BaseDAO<SpecialCategory> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public SpecialCategoryDao() {
        ID = "categoryId";
        clazz = SpecialCategory.class;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
