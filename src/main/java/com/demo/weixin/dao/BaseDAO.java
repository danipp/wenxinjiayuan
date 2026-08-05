package com.demo.weixin.dao;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.demo.common.core.util.SnowflakeIdGenerator;
import com.demo.weixin.entity.Base;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
public abstract class BaseDAO<T> {

    protected String ID = "id";

    protected String DEL_FLAG = "del_flag";

    protected Class<T> clazz;

    private boolean needCheckRepeatedId = true;

    /**
     * 安全地为Criteria追加del_flag条件。
     * 使用andOperator创建新的Criteria组合原始条件和del_flag条件，
     * 不修改原始Criteria对象，避免Criteria复用时重复添加del_flag导致报错。
     *
     * @param criteria 原始查询条件（不会被修改）
     * @return 组合了del_flag条件的新Criteria
     */
    protected Criteria withDelFlag(Criteria criteria) {
        return new Criteria().andOperator(criteria, Criteria.where(DEL_FLAG).is(false));
    }

    public abstract MongoTemplate getMongoTemplate();

    public <T> T findById(Long id) {
        Query query = new Query(Criteria.where(ID).is(id).and(DEL_FLAG).is(false));
        List<T> list = (List<T>) getMongoTemplate().find(query, clazz);
        if (list != null && list.size() > 0) {
            if (list.size() > 1) {
                throw new IllegalArgumentException("id=" + id + " is duplicate!,find more than 1 item.");
            }
            return list.get(0);
        }
        return null;
    }

    public T insertDocument(T base) {
        Long id = ((Base) base).getID();
        if (id == null || id == 0L) {
            //使用雪花算法 生成一个唯一的id
            id = SnowflakeIdGenerator.getInstance().nextId();
            ((Base) base).setID(id);
        }
        if (needCheckRepeatedId) {
            //是否需要检查Id 唯一性，如果业务层用了唯一id生成器，可以不检查.....
            T x = findById(id);
            if (x != null) {
                //防止id 重复...
                throw new IllegalArgumentException("id=" + id + " is duplicate! for clazz:" + clazz);
            }
        }

        //设置逻辑删除标志 false    
        DateTime date = DateUtil.date();
        ((Base) base).setDel_flag(false);

        if (((Base) base).getCreateTime() == null) {
            ((Base) base).setCreateTime(date);
        }
        if (((Base) base).getUpdateTime() == null) {
            ((Base) base).setUpdateTime(date);
        }

        return getMongoTemplate().insert(base);
    }

    public Collection<T> insertBatch(Collection<T> collection) {
        if(CollectionUtil.isEmpty(collection)){
            return null;
        }
        for(T base:collection){
            Long id = ((Base) base).getID();
            if (id == null || id == 0L) {
                //使用雪花算法 生成一个唯一的id
                id = SnowflakeIdGenerator.getInstance().nextId();
                ((Base) base).setID(id);
            }
            if (needCheckRepeatedId) {
                //是否需要检查Id 唯一性，如果业务层用了唯一id生成器，可以不检查.....
                T x = findById(id);
                if (x != null) {
                    //防止id 重复...
                    throw new IllegalArgumentException("id=" + id + " is duplicate! for clazz:" + clazz);
                }
            }

            //设置逻辑删除标志 false
            DateTime date = DateUtil.date();
            ((Base) base).setDel_flag(false);

            if (((Base) base).getCreateTime() == null) {
                ((Base) base).setCreateTime(date);
            }
            if (((Base) base).getUpdateTime() == null) {
                ((Base) base).setUpdateTime(date);
            }
        }
        return getMongoTemplate().insertAll(collection);
    }

    public T updateDocument(Map<String, Object> updateMap) {
        if (updateMap.get(ID) == null) {
            throw new IllegalArgumentException("id is null!");
        }
        Object o = updateMap.get(ID);
        Query query;
        updateMap.remove(ID);
        updateMap.put("updateTime", DateUtil.date());
        Update update = new Update();
        updateMap.keySet().stream().forEach(key -> {
            update.set(key, updateMap.get(key));
        });
        if (o instanceof Collection<?>) {
            query = new Query(Criteria.where(ID).in((Collection<?>) o).and(DEL_FLAG).is(false));
            getMongoTemplate().updateMulti(query, update, clazz);
        } else {
            query = new Query(Criteria.where(ID).is(o).and(DEL_FLAG).is(false));
            getMongoTemplate().upsert(query, update, clazz);
        }
        List<T> list = getMongoTemplate().find(query, clazz);
        return list.get(0);
    }


    /**
     * true 写入成功
     *
     * @param criteria
     * @return
     */
    public Boolean updateOneDocument(Criteria criteria, Update update) {
        Query query = new Query(withDelFlag(criteria));
        update.set("updateTime", DateUtil.date());
        UpdateResult upsert = getMongoTemplate().updateFirst(query, update, clazz);
        return upsert.getModifiedCount() > 0;
    }

    public T saveOrUpdate(T base) {
        Long id = ((Base) base).getID();
        DateTime date = DateUtil.date();
        if (id == null || id == 0L) {
            //使用雪花算法 生成一个唯一的id
            id = SnowflakeIdGenerator.getInstance().nextId();
            ((Base) base).setID(id);
            ((Base) base).setDel_flag(false);
            ((Base) base).setCreateTime(date);
            ((Base) base).setUpdateTime(date);
            return getMongoTemplate().save(base);
        }
        Query query = new Query(Criteria.where(ID).is(((Base) base).getID()).and(DEL_FLAG).is(false));
        Update update = new Update();
        // 使用反射获取所有字段并添加到 Update 对象中
        java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            field.setAccessible(true); // 允许访问私有字段
            try {
                Object value = field.get(base);
                if (value != null) {
                    update.set(field.getName(), value); // 更新字段
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        update.set("updateTime", DateUtil.date());
        return getMongoTemplate().findAndModify(query, update, FindAndModifyOptions.options().upsert(true), clazz);
    }

    /**
     * null也写到库
     *
     * @param base
     * @return
     */

    public T update(T base) {
        Long id = ((Base) base).getID();
        DateTime date = DateUtil.date();
        if (id == null || id == 0L) {
            //使用雪花算法 生成一个唯一的id
            id = SnowflakeIdGenerator.getInstance().nextId();
            ((Base) base).setID(id);
            ((Base) base).setDel_flag(false);
            ((Base) base).setCreateTime(date);
            ((Base) base).setUpdateTime(date);
            return getMongoTemplate().save(base);
        }
        Query query = new Query(Criteria.where(ID).is(((Base) base).getID()).and(DEL_FLAG).is(false));
        Update update = new Update();
        // 使用反射获取所有字段并添加到 Update 对象中
        java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            field.setAccessible(true); // 允许访问私有字段
            try {
                Object value = field.get(base);
                update.set(field.getName(), value); // 更新字段
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        update.set("updateTime", DateUtil.date());
        return getMongoTemplate().findAndModify(query, update, FindAndModifyOptions.options().upsert(true), clazz);
    }

    public T updateDocument(Criteria criteria, Update update) {
        Query query = new Query(withDelFlag(criteria));
        update.set("updateTime", DateUtil.date());
        return getMongoTemplate().findAndModify(query, update, clazz);
    }

    public UpdateResult updateMulti(Criteria criteria, Update update) {
        Query query = new Query(withDelFlag(criteria));
        update.set("updateTime", DateUtil.date());
        return getMongoTemplate().updateMulti(query, update, clazz);
    }

    /**
     * 逻辑删除
     *
     * @param id
     */
    public void deleteDocument(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null!");
        }

        Query query = new Query(Criteria.where(ID).is(id).and(DEL_FLAG).is(false));
        Update update = new Update().set("del_flag", true).set("updateTime", DateUtil.date());
        T xx = getMongoTemplate().findAndModify(query, update, clazz);
        if (log.isDebugEnabled()) {
            log.debug("deleteDocument:,id={},{}", id, xx);
        }
    }

    public void deleteDocument(Criteria criteria) {
        Query query = new Query(withDelFlag(criteria));
        Update update = new Update().set("del_flag", true).set("updateTime", DateUtil.date());
        getMongoTemplate().updateMulti(query, update, clazz);
    }

    /**
     * 逻辑删除
     *
     * @param ids
     */
    public void batchDeleteDocument(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            throw new IllegalArgumentException("ids is empty!");
        }
        Query query = new Query(Criteria.where(ID).in(ids).and(DEL_FLAG).is(false));
        Update update = new Update().set("del_flag", true).set("updateTime", DateUtil.date());
        getMongoTemplate().updateMulti(query, update, clazz);
        // findAndModify 是 MongoDB 的原子操作，设计初衷是查询并修改单个文档（匹配查询的第一条记录）。
        //如果需要批量更新多条文档，应使用 updateMulti(Query query, UpdateDefinition update, Class<?> entityClass) 方法。
    }

    /**
     * 物理删除（真删除）
     *
     * @param id
     */
    public void deleteDocumentPhisiclly(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null!");
        }
        Query query = new Query(Criteria.where(ID).is(id));
        List<T> xx = getMongoTemplate().findAllAndRemove(query, clazz);
        if (xx == null) {
            xx = new ArrayList<>(0);
        }
        if (log.isDebugEnabled()) {
            log.debug("deleteDocumentPhisiclly:,id={}, count={}", id, xx.size());
        }
    }

    public void deleteDocumentPhisiclly(Criteria criteria) {
        Query query = new Query(criteria);
        getMongoTemplate().remove(query, clazz);
    }

    public void batchDeleteDocumentPhisiclly(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            throw new IllegalArgumentException("ids is empty!");
        }
        Query query = new Query(Criteria.where(ID).in(ids));
        getMongoTemplate().remove(query, clazz);
    }

    /**
     * @param criteria 查询条件
     * @return map格式的链表 （相对于业务对象类型的查询方法，可以查到原生的更多内容）
     */
    public List<Map> findDocumentMap(Criteria criteria) {
        Query query = new Query(withDelFlag(criteria));

        Document documentAnnotation = clazz.getAnnotation(Document.class);
        return getMongoTemplate().find(query, Map.class, documentAnnotation.collection());
    }

    /**
     * 返回自己想要的字段
     *
     * @param criteria
     * @param fieldName
     * @return
     */
    public List<Map> findDocumentMapByFields(Criteria criteria, String... fieldName) {
        Query query = new Query(withDelFlag(criteria));
        Field fields = query.fields().exclude("_id");
        fields.include(fieldName);
        Document documentAnnotation = clazz.getAnnotation(Document.class);
        return getMongoTemplate().find(query, Map.class, documentAnnotation.collection());
    }

    /**
     * @param criteria 查询条件
     * @return 单个实体
     */
    public T findOne(Criteria criteria) {
        Query query = new Query(withDelFlag(criteria));
        Document documentAnnotation = clazz.getAnnotation(Document.class);
        return getMongoTemplate().findOne(query, clazz, documentAnnotation.collection());
    }

    public T findOne(Criteria criteria,Sort sort) {
        Query query = new Query(withDelFlag(criteria)).with(sort).limit(1);
        Document documentAnnotation = clazz.getAnnotation(Document.class);
        return getMongoTemplate().findOne(query, clazz, documentAnnotation.collection());
    }


    /**
     * @param criteria 查询条件
     * @return map格式的链表
     */
    public List<T> findDocumentList(Criteria criteria) {

        Query query = new Query(withDelFlag(criteria));

        Document documentAnnotation = clazz.getAnnotation(Document.class);
        return getMongoTemplate().find(query, clazz, documentAnnotation.collection());
    }

    public List<T> findDocumentList(Criteria criteria,int limit) {

        Query query = new Query(withDelFlag(criteria));
        query.limit(limit);
        Document documentAnnotation = clazz.getAnnotation(Document.class);
        return getMongoTemplate().find(query, clazz, documentAnnotation.collection());
    }
    public List<T> findDocumentList(Criteria criteria,Sort sort,int limit) {

        Query query = new Query(withDelFlag(criteria));
        query.with(sort);
        query.limit(limit);
        Document documentAnnotation = clazz.getAnnotation(Document.class);
        return getMongoTemplate().find(query, clazz, documentAnnotation.collection());
    }

    public GeoResults<T> geoNear(NearQuery query) {
        Document documentAnnotation = clazz.getAnnotation(Document.class);
        return getMongoTemplate().geoNear(query, clazz, documentAnnotation.collection());
    }

    public List<T> findDocumentList(Criteria criteria, Sort.Order... orders) {

        Query query = new Query(withDelFlag(criteria)).with(Sort.by(orders));
        Document documentAnnotation = clazz.getAnnotation(Document.class);
        return getMongoTemplate().find(query, clazz, documentAnnotation.collection());
    }

    public List<Long> findDistinct(Criteria criteria,String field) {

        Query query = new Query(withDelFlag(criteria));
        return getMongoTemplate().findDistinct(query, field,clazz, Long.class);
    }


    /**
     * @param pageable
     * @param criteria 查询条件
     * @return map格式的链表
     */
    public Page<T> findDocumentPage(Criteria criteria, Pageable pageable) {
        Query query = new Query(withDelFlag(criteria));

        //不分页的总数
        final long total = getMongoTemplate().count(query, clazz);


        query.with(pageable);
        if (pageable.getSort() != null) {
            query.with(pageable.getSort());
        }
        Document documentAnnotation = clazz.getAnnotation(Document.class);
        List<T> list = getMongoTemplate().find(query, clazz, documentAnnotation.collection());

        Page<T> page = new PageImpl<>(list, pageable, total);

        return page;
    }

    public long count(Criteria criteria) {
        Query query = new Query(withDelFlag(criteria));
        Document documentAnnotation = clazz.getAnnotation(Document.class);
        return getMongoTemplate().count(query, documentAnnotation.collection());
    }

    /**
     * 字段合算
     *
     * @param criteria
     * @param field
     * @return
     */
    public Object sumByField(Criteria criteria, String field) {
        Criteria safeCriteria = withDelFlag(criteria);
        Document documentAnnotation = clazz.getAnnotation(Document.class);
        // 创建聚合管道
        GroupOperation groupOperation = Aggregation.group().sum(field).as("total"); // 直接求和，MongoDB 会自动处理 null
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(safeCriteria), // 添加过滤条件
                groupOperation // 聚合并计算总和
        );

        // 执行聚合查询
        AggregationResults<org.bson.Document> results = getMongoTemplate().aggregate(aggregation, documentAnnotation.collection(), org.bson.Document.class);
        // 获取总和
        return results.getMappedResults().isEmpty() ? 0 : results.getMappedResults().get(0).get("total");
    }

    /**
     * 分组合算
     *
     * @param criteria
     * @param groupByField
     * @param sumField
     * @param limit
     * @return
     */
    public List<HashMap> groupSumDescByField(Criteria criteria, String groupByField, String sumField, int limit) {
        Criteria safeCriteria = withDelFlag(criteria);
        // 创建聚合管道
        GroupOperation groupOperation = Aggregation.group(groupByField).sum(sumField).as("total"); // 按指定字段分组并求和
        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Order.desc("total"))); // 按总分数降序排序
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(safeCriteria), // 添加过滤条件
                groupOperation, // 进行分组
                sortOperation, // 排序
                Aggregation.limit(limit) // 限制结果数量
        );
        Document documentAnnotation = clazz.getAnnotation(Document.class);
        // 执行聚合查询
        AggregationResults<HashMap> results = getMongoTemplate().aggregate(aggregation, documentAnnotation.collection(), HashMap.class);

        // 返回前10个结果
        return results.getMappedResults();
    }

    /**
     * 分组统计
     *
     * @param criteria
     * @param groupByField
     * @param limit
     * @param fieldArray
     * @return
     */

    public List<HashMap<String, Object>> groupCountDescByField(Criteria criteria, String groupByField, int limit, boolean fieldArray) {
        Criteria safeCriteria = withDelFlag(criteria);
        // 按指定字段分组并求和
        GroupOperation groupOperation = Aggregation.group(groupByField).count().as("total");
        // 降序排序
        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Order.desc("total")));
        Aggregation aggregation;
        if (fieldArray) {
            // 使用 $unwind 展开数组
            UnwindOperation unwindOperation = Aggregation.unwind(groupByField);
            aggregation = Aggregation.newAggregation(Aggregation.match(safeCriteria), // 添加过滤条件
                    unwindOperation, // 展开数组
                    groupOperation, // 进行分组
                    sortOperation, // 排序
                    Aggregation.limit(limit) // 限制结果数量
            );
        } else {
            aggregation = Aggregation.newAggregation(Aggregation.match(safeCriteria), // 添加过滤条件
                    groupOperation, // 进行分组
                    sortOperation, // 排序
                    Aggregation.limit(limit) // 限制结果数量
            );
        }
        Document documentAnnotation = clazz.getAnnotation(Document.class);
        AggregationResults<HashMap> results = getMongoTemplate().aggregate(aggregation, documentAnnotation.collection(), HashMap.class);
        List<HashMap<String, Object>> output = new ArrayList<>();
        for (HashMap<String, Object> result : results.getMappedResults()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put(groupByField, result.get("_id"));
            map.put("total", result.get("total"));
            output.add(map);
        }
        return output;
    }

    /**
     * 分组统计并计算比例
     *
     * @param criteria
     * @param groupByField
     * @param fieldArray
     * @return
     */
    public List<HashMap<String, Object>> groupCountsWithPercentage(Criteria criteria, String groupByField, boolean fieldArray) {
        Criteria safeCriteria = withDelFlag(criteria);
        // 按指定字段分组并求和
        GroupOperation groupOperation = Aggregation.group(groupByField).count().as("total");
        // 降序排序
        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Order.desc("total")));
        Aggregation aggregation;
        if (fieldArray) {
            // 使用 $unwind 展开数组
            UnwindOperation unwindOperation = Aggregation.unwind(groupByField);
            aggregation = Aggregation.newAggregation(Aggregation.match(safeCriteria), // 添加过滤条件
                    unwindOperation, // 展开数组
                    groupOperation, // 进行分组
                    sortOperation);
        } else {
            aggregation = Aggregation.newAggregation(Aggregation.match(safeCriteria), // 添加过滤条件
                    groupOperation, // 进行分组
                    sortOperation);
        }
        Document documentAnnotation = clazz.getAnnotation(Document.class);
        AggregationResults<HashMap> results = getMongoTemplate().aggregate(aggregation, documentAnnotation.collection(), HashMap.class);
        // 计算总数
        long totalCount = results.getMappedResults().stream().mapToLong(result -> MapUtil.getLong(result, "total", 0L)).sum();
        // 处理结果并计算比例
        List<HashMap<String, Object>> output = new ArrayList<>();
        for (HashMap<String, Object> result : results.getMappedResults()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put(groupByField, result.get("_id"));
            long count = MapUtil.getLong(result, "total", 0L);
            map.put("total", count); // 添加计数结果
            BigDecimal percentage = totalCount > 0 ? BigDecimal.valueOf(count).divide(BigDecimal.valueOf(totalCount), 3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) : BigDecimal.ZERO;
            map.put("percentage", percentage.toString()); // 计算比例
            output.add(map);
        }
        return output;
    }


}
