package com.demo.weixin.service.mine;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.demo.weixin.dao.UserDao;
import com.demo.weixin.dao.activity.ActivityDao;
import com.demo.weixin.entity.User;
import com.demo.weixin.enums.mine.DemandStatusEnum;
import com.demo.weixin.vo.mine.LeaderboardItemVO;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 达人排行榜服务
 * 使用 MongoTemplate 做聚合查询，支持两类排行榜：
 * - 互助达人：聚合 DemandRecord，按帮忙次数排序，附带平均评分
 * - 活动达人：聚合 ActivitySignup，按参与活动次数排序，支持按社区筛选
 */
@Service
@Slf4j
public class LeaderboardService {

    /** 排行榜类型：互助达人 */
    private static final int TYPE_HELPER = 1;
    /** 排行榜类型：活动达人 */
    private static final int TYPE_ACTIVITY = 2;

    /** 默认限制条数 */
    private static final int DEFAULT_LIMIT = 20;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ActivityDao activityDao;

    /**
     * 获取达人排行榜
     * 根据类型分发到互助达人或活动达人查询。
     *
     * @param type      排行榜类型：1互助达人 2活动达人
     * @param community 社区名称（可选，仅活动达人支持按社区筛选）
     * @param limit     限制条数
     * @return 排行榜条目列表
     */
    public List<LeaderboardItemVO> getLeaderboard(Integer type, String community, Integer limit) {
        // 限制条数兜底
        if (limit == null || limit <= 0) {
            limit = DEFAULT_LIMIT;
        }
        if (type != null && type == TYPE_ACTIVITY) {
            // 活动达人
            return getActivityLeaderboard(community, limit);
        }
        // 默认查询互助达人
        return getHelperLeaderboard(limit);
    }

    /**
     * 互助达人排行榜
     * 聚合 DemandRecord 集合：
     * - 匹配条件：状态为待评价或已完成，且 helperUserId 不为空
     * - 按 helperUserId 分组：count 统计帮忙次数，avgRating 统计平均评分
     * - 按帮忙次数降序排序，取前 limit 条
     * 最后批量查询 User 表补充 nickName 和 avatar。
     * TODO demandRecord 集合应在 helperUserId + status + del_flag 上建立复合索引以优化聚合查询性能
     * （注：已在 DemandRecord 实体上通过 @CompoundIndex(idx_demand_helper) 声明该索引）
     *
     * @param limit 限制条数
     * @return 互助达人排行榜列表
     */
    private List<LeaderboardItemVO> getHelperLeaderboard(int limit) {
        // 1. 构建匹配条件：未删除、状态为待评价或已完成、帮忙者不为空
        Criteria matchCriteria = new Criteria().andOperator(
                Criteria.where("del_flag").is(false),
                Criteria.where("status").in(
                        DemandStatusEnum.TO_EVALUATE.getCode(),
                        DemandStatusEnum.COMPLETED.getCode()),
                Criteria.where("helperUserId").ne(null)
        );
        // 2. 按 helperUserId 分组：统计帮忙次数和平均评分
        GroupOperation groupOperation = Aggregation.group("helperUserId")
                .count().as("count")
                .avg("rating").as("avgRating");
        // 3. 按帮忙次数降序排序
        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Order.desc("count")));
        // 4. 限制条数
        LimitOperation limitOperation = Aggregation.limit(limit);
        // 5. 组装聚合管道
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(matchCriteria),
                groupOperation,
                sortOperation,
                limitOperation
        );
        // 6. 执行聚合查询
        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation, "demandRecord", Document.class);
        List<Document> mappedResults = results.getMappedResults();
        // 7. 收集所有帮忙者ID，批量查询用户信息
        List<Long> helperUserIds = new ArrayList<>();
        for (Document doc : mappedResults) {
            Long userId = toLong(doc.get("_id"));
            if (userId != null) {
                helperUserIds.add(userId);
            }
        }
        Map<Long, User> userMap = batchQueryUsers(helperUserIds);
        // 8. 组装排行榜结果
        List<LeaderboardItemVO> list = new ArrayList<>();
        for (Document doc : mappedResults) {
            LeaderboardItemVO item = new LeaderboardItemVO();
            Long userId = toLong(doc.get("_id"));
            item.setUserId(userId);
            item.setCount(doc.getInteger("count"));
            // 平均评分（MongoDB 的 $avg 会忽略 null 值）
            item.setAvgRating(toDouble(doc.get("avgRating")));
            User user = userMap.get(userId);
            if (user != null) {
                item.setNickName(user.getNickName());
                item.setAvatar(user.getAvatar());
            }
            list.add(item);
        }
        return list;
    }

    /**
     * 活动达人排行榜
     * 聚合 ActivitySignup 集合：
     * - 若指定社区：先查 Activity 集合找到该社区所有 activityId，再用这些 activityId 筛选报名记录
     * - 按 userId 分组：count 统计参与活动次数
     * - 按参与次数降序排序，取前 limit 条
     * 最后批量查询 User 表补充 nickName 和 avatar。
     *
     * @param community 社区名称（可选）
     * @param limit     限制条数
     * @return 活动达人排行榜列表
     */
    private List<LeaderboardItemVO> getActivityLeaderboard(String community, int limit) {
        // 1. 构建匹配条件
        Criteria matchCriteria;
        if (StrUtil.isNotBlank(community)) {
            // 指定社区：先查出该社区下所有活动ID
            List<Long> activityIds = activityDao.findDistinct(
                    Criteria.where("community").is(community), "activityId");
            if (CollectionUtil.isEmpty(activityIds)) {
                // 该社区下无活动，直接返回空列表
                return new ArrayList<>();
            }
            matchCriteria = new Criteria().andOperator(
                    Criteria.where("del_flag").is(false),
                    Criteria.where("activityId").in(activityIds)
            );
        } else {
            // 未指定社区：查询全部未删除的报名记录
            matchCriteria = Criteria.where("del_flag").is(false);
        }
        // 2. 按 userId 分组：统计参与活动次数
        GroupOperation groupOperation = Aggregation.group("userId")
                .count().as("count");
        // 3. 按参与次数降序排序
        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Order.desc("count")));
        // 4. 限制条数
        LimitOperation limitOperation = Aggregation.limit(limit);
        // 5. 组装聚合管道
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(matchCriteria),
                groupOperation,
                sortOperation,
                limitOperation
        );
        // 6. 执行聚合查询
        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation, "activitySignup", Document.class);
        List<Document> mappedResults = results.getMappedResults();
        // 7. 收集所有用户ID，批量查询用户信息
        List<Long> userIds = new ArrayList<>();
        for (Document doc : mappedResults) {
            Long userId = toLong(doc.get("_id"));
            if (userId != null) {
                userIds.add(userId);
            }
        }
        Map<Long, User> userMap = batchQueryUsers(userIds);
        // 8. 组装排行榜结果
        List<LeaderboardItemVO> list = new ArrayList<>();
        for (Document doc : mappedResults) {
            LeaderboardItemVO item = new LeaderboardItemVO();
            Long userId = toLong(doc.get("_id"));
            item.setUserId(userId);
            item.setCount(doc.getInteger("count"));
            User user = userMap.get(userId);
            if (user != null) {
                item.setNickName(user.getNickName());
                item.setAvatar(user.getAvatar());
            }
            list.add(item);
        }
        return list;
    }

    /**
     * 批量查询用户信息并构建 userId -> User 映射
     *
     * @param userIds 用户ID列表
     * @return 用户映射（key=userId）
     */
    private Map<Long, User> batchQueryUsers(List<Long> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        List<User> users = userDao.findDocumentList(Criteria.where("userId").in(userIds));
        return users.stream()
                .collect(Collectors.toMap(User::getUserId, u -> u, (a, b) -> a));
    }

    /**
     * 将聚合结果中的数值安全转为 Long
     * MongoDB 返回的数值类型可能是 Integer/Long/Double，统一转换避免类型异常。
     *
     * @param value 原始值
     * @return Long 值（无法转换时返回 null）
     */
    private Long toLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    /**
     * 将聚合结果中的数值安全转为 Double
     *
     * @param value 原始值
     * @return Double 值（无法转换时返回 null）
     */
    private Double toDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null;
    }
}
