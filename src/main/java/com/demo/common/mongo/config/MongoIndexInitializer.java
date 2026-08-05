package com.demo.common.mongo.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MongoDB 索引初始化器。
 * 应用启动时自动检查并创建业务集合的索引，避免全表扫描导致性能问题。
 * 使用 ApplicationRunner 确保在 Spring 容器完全初始化后执行。
 * 幂等设计：已存在的索引会被自动跳过，不会重复创建。
 */
@Slf4j
@Component
public class MongoIndexInitializer implements ApplicationRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(ApplicationArguments args) {
        log.info("开始检查 MongoDB 索引...");
        int created = 0;

        // power_order 索引
        created += createIndex("power_order", "idx_status_expectedEndTime",
                Map.of("status", 1, "expectedEndTime", 1));
        created += createIndex("power_order", "idx_status_expireTime",
                Map.of("status", 1, "expireTime", 1));
        created += createIndex("power_order", "idx_merchantId_createTime",
                Map.of("merchantId", 1, "createTime", -1));
        created += createIndex("power_order", "idx_developerId_createTime",
                Map.of("developerId", 1, "createTime", -1));
        created += createIndex("power_order", "idx_agentId_createTime",
                Map.of("agentId", 1, "createTime", -1));
        created += createIndex("power_order", "idx_userId_deviceSn_status",
                Map.of("userId", 1, "deviceSn", 1, "status", 1));
        created += createIndex("power_order", "idx_deviceId_status",
                Map.of("deviceId", 1, "status", 1));

        // power_device 索引
        created += createUniqueIndex("power_device", "idx_sn_unique",
                Map.of("sn", 1));
        created += createIndex("power_device", "idx_merchantId",
                Map.of("merchantId", 1));
        created += createIndex("power_device", "idx_developerId",
                Map.of("developerId", 1));

        // power_device_profile 索引
        created += createUniqueIndex("power_device_profile", "idx_sn_unique",
                Map.of("sn", 1));
        created += createIndex("power_device_profile", "idx_status",
                Map.of("status", 1));

        // power_merchant 索引
        created += createIndex("power_merchant", "idx_developerId",
                Map.of("developerId", 1));
        created += createIndex("power_merchant", "idx_userId",
                Map.of("userId", 1));

        // power_developer 索引
        created += createIndex("power_developer", "idx_agentId",
                Map.of("agentId", 1));
        created += createIndex("power_developer", "idx_userId",
                Map.of("userId", 1));
        created += createIndex("power_developer", "idx_cellphone",
                Map.of("cellphone", 1));

        // power_agent 索引
        created += createIndex("power_agent", "idx_userId",
                Map.of("userId", 1));

        // power_wallet 索引
        created += createIndex("power_wallet", "idx_userId_role",
                Map.of("userId", 1, "role", 1));

        // power_withdraw_record 索引
        created += createIndex("power_withdraw_record", "idx_userId_role_status",
                Map.of("userId", 1, "role", 1, "status", 1));

        log.info("MongoDB 索引检查完成，新建索引数={}", created);
    }

    /**
     * 创建普通复合索引（幂等，已存在则跳过）。
     *
     * @param collection 集合名
     * @param indexName  索引名
     * @param fields     字段与排序方向（1=升序，-1=降序）
     * @return 1=新建，0=已存在跳过
     */
    private int createIndex(String collection, String indexName, Map<String, Integer> fields) {
        try {
            if (indexExists(collection, indexName)) {
                return 0;
            }
            Document indexKeys = new Document();
            fields.forEach(indexKeys::append);
            mongoTemplate.getCollection(collection).createIndex(indexKeys, new IndexOptions().name(indexName));
            log.info("创建索引：{}.{}", collection, indexName);
            return 1;
        } catch (Exception e) {
            log.warn("创建索引失败 {}:{} - {}", collection, indexName, e.getMessage());
            return 0;
        }
    }

    /**
     * 创建唯一索引（幂等，已存在则跳过）。
     *
     * @param collection 集合名
     * @param indexName  索引名
     * @param fields     字段与排序方向
     * @return 1=新建，0=已存在跳过
     */
    private int createUniqueIndex(String collection, String indexName, Map<String, Integer> fields) {
        try {
            if (indexExists(collection, indexName)) {
                return 0;
            }
            Document indexKeys = new Document();
            fields.forEach(indexKeys::append);
            mongoTemplate.getCollection(collection).createIndex(indexKeys,
                    new IndexOptions().name(indexName).unique(true));
            log.info("创建唯一索引：{}.{}", collection, indexName);
            return 1;
        } catch (Exception e) {
            log.warn("创建唯一索引失败 {}:{} - {}", collection, indexName, e.getMessage());
            return 0;
        }
    }

    /**
     * 检查索引是否已存在。
     *
     * @param collection 集合名
     * @param indexName  索引名
     * @return true=已存在
     */
    private boolean indexExists(String collection, String indexName) {
        for (Document index : mongoTemplate.getCollection(collection).listIndexes()) {
            if (indexName.equals(index.getString("name"))) {
                return true;
            }
        }
        return false;
    }
}
