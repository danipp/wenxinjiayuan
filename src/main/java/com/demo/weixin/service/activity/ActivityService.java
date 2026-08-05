package com.demo.weixin.service.activity;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.UserDao;
import com.demo.weixin.dao.activity.ActivityDao;
import com.demo.weixin.dao.activity.ActivitySignupDao;
import com.demo.weixin.entity.User;
import com.demo.weixin.entity.activity.Activity;
import com.demo.weixin.entity.activity.ActivitySignup;
import com.demo.weixin.enums.activity.ActivityStatusEnum;
import com.demo.weixin.enums.activity.ActivityTypeEnum;
import com.demo.weixin.vo.activity.ActivityCreateVO;
import com.demo.weixin.vo.activity.ActivityQueryVO;
import com.demo.weixin.vo.activity.MyActivityQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 活动核心服务
 * <p>
 * 管理活动全生命周期：创建、查询（详情/广场/我的活动）、报名、状态更新。
 * 活动状态根据 startTime/endTime 实时计算，status 字段为持久化快照，可由定时任务批量刷新。
 * 报名操作通过条件更新保证并发安全：只有参与人数未超限时才递增成功。
 * </p>
 */
@Service
@Slf4j
public class ActivityService {

    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private ActivitySignupDao activitySignupDao;
    @Autowired
    private UserDao userDao;

    /**
     * 创建活动
     * 校验入参后构建活动实体，根据 startTime/endTime 计算初始状态，参与人数初始化为0。
     *
     * @param userId 发布者用户ID（当前登录用户）
     * @param vo     创建活动入参
     * @return 创建后的活动
     */
    public Activity createActivity(Long userId, ActivityCreateVO vo) {
        // 1. 参数校验
        validateCreateVO(vo);
        // 2. 构建活动实体
        Activity activity = new Activity();
        activity.setPublisherUserId(userId);
        activity.setTitle(vo.getTitle());
        activity.setContent(vo.getContent());
        activity.setLocation(vo.getLocation());
        activity.setStartTime(vo.getStartTime());
        activity.setEndTime(vo.getEndTime());
        activity.setCommunity(vo.getCommunity());
        // [新增 2026-08-03 17:30] 设置社区ID用于数据隔离
        activity.setCommunityId(vo.getCommunityId());
        activity.setMaxLimit(vo.getMaxLimit() != null ? vo.getMaxLimit() : 0);
        activity.setCollectPhone(vo.getCollectPhone() != null ? vo.getCollectPhone() : false);
        activity.setType(vo.getType() != null ? vo.getType() : 2);
        activity.setCoverImage(vo.getCoverImage());
        activity.setTag(vo.getTag());
        activity.setParticipantCount(0);
        // 3. 根据时间计算初始状态
        ActivityStatusEnum statusEnum = ActivityStatusEnum.computeStatus(vo.getStartTime(), vo.getEndTime());
        activity.setStatus(statusEnum.getCode());
        // 4. 持久化
        activityDao.insertDocument(activity);
        log.info("创建活动，activityId={}，title={}，publisherUserId={}", activity.getActivityId(), activity.getTitle(), userId);
        return activity;
    }

    /**
     * 获取活动详情（含当前用户报名状态）
     * 实时计算活动状态和状态文本，查询当前用户是否已报名。
     *
     * @param activityId 活动ID
     * @param userId     当前登录用户ID
     * @return 活动详情（含 signedUp、statusText）
     */
    public Activity getActivityDetail(Long activityId, Long userId) {
        Activity activity = activityDao.findById(activityId);
        if (activity == null) {
            throw new BizException("活动不存在");
        }
        // 实时计算状态
        populateStatus(activity);
        // 查询当前用户是否已报名
        if (userId != null) {
            ActivitySignup signup = activitySignupDao.findOne(
                    Criteria.where("activityId").is(activityId)
                            .and("userId").is(userId));
            activity.setSignedUp(signup != null);
        } else {
            activity.setSignedUp(false);
        }
        // [新增 2026-08-03 18:20] 填充发布者信息（昵称、头像），与广场列表保持一致
        populateAuthorInfo(Collections.singletonList(activity));
        return activity;
    }

    /**
     * 活动广场列表（带筛选）
     * 支持 sort（排序方式）、range（参与人数范围）、type（活动类型）筛选。
     * 注意：当前返回所有活动（包括已结束的），业务上可能需要展示历史活动；
     * 如需只展示未结束活动，可在 criteria 中增加状态过滤条件。
     *
     * @param queryVO 查询入参
     * @return 分页活动列表（含 authorName、authorAvatar）
     */
    public Page<Activity> getSquareList(ActivityQueryVO queryVO) {
        // 1. 构建排序
        Sort sort = buildSort(queryVO.getSort());
        Pageable pageable = PageRequest.of(queryVO.getPageNumber(), queryVO.getPageSize(), sort);
        // 2. 构建查询条件
        Criteria criteria = new Criteria();
        // [新增 2026-08-03 17:30] 社区数据隔离：按communityId过滤
        if (queryVO.getCommunityId() != null) {
            criteria.and("communityId").is(queryVO.getCommunityId());
        }
        // 活动类型筛选
        if (queryVO.getType() != null) {
            criteria.and("type").is(queryVO.getType());
        }
        // 参与人数范围筛选
        applyRangeFilter(criteria, queryVO.getRange());
        // 3. 分页查询
        Page<Activity> page = activityDao.findDocumentPage(criteria, pageable);
        // 4. 批量填充发布者信息
        populateAuthorInfo(page.getContent());
        return page;
    }

    /**
     * 报名活动（需要并发控制）
     * 校验：活动是否存在、是否已结束、是否已报名、是否超过人数限制。
     * 操作顺序：先插入报名记录，再原子递增 participantCount（带人数上限条件）；
     * 若递增失败（人数已满）则回滚删除刚插入的报名记录，保证 participantCount 与报名记录一致。
     *
     * @param activityId 活动ID
     * @param userId     报名用户ID
     * @return 报名记录
     */
    public ActivitySignup signup(Long activityId, Long userId) {
        // 1. 查询并校验活动
        Activity activity = activityDao.findById(activityId);
        if (activity == null) {
            throw new BizException("活动不存在");
        }
        // 2. 校验活动是否已结束
        ActivityStatusEnum currentStatus = ActivityStatusEnum.computeStatus(activity.getStartTime(), activity.getEndTime());
        if (currentStatus == ActivityStatusEnum.ENDED) {
            throw new BizException("活动已结束，无法报名");
        }
        // 3. 校验是否已报名
        ActivitySignup existSignup = activitySignupDao.findOne(
                Criteria.where("activityId").is(activityId)
                        .and("userId").is(userId));
        if (existSignup != null) {
            throw new BizException("您已报名该活动，请勿重复报名");
        }
        // 4. 获取用户信息（冗余到报名记录）
        User user = userDao.findById(userId);
        // 5. 先插入报名记录（先落库报名信息，再递增人数，避免递增成功但报名记录插入失败导致计数不一致）
        ActivitySignup signup = new ActivitySignup();
        signup.setActivityId(activityId);
        signup.setUserId(userId);
        signup.setNickName(user != null ? user.getNickName() : "匿名用户");
        signup.setAvatar(user != null ? user.getAvatar() : null);
        // 如果活动需要收集手机号，从用户信息中获取
        if (Boolean.TRUE.equals(activity.getCollectPhone()) && user != null) {
            signup.setPhone(user.getCellphone());
        }
        signup.setJoinTime(new Date());
        activitySignupDao.insertDocument(signup);
        // 6. 再原子递增 participantCount（带条件：maxLimit > 0 时校验不超限）
        int maxLimit = activity.getMaxLimit() != null ? activity.getMaxLimit() : 0;
        Criteria incCriteria = Criteria.where("activityId").is(activityId);
        if (maxLimit > 0) {
            // 有人数限制时，只有 participantCount < maxLimit 才能递增
            incCriteria.and("participantCount").lt(maxLimit);
        }
        Boolean success = activityDao.updateOneDocument(incCriteria, new Update().inc("participantCount", 1));
        if (!success) {
            // 递增失败（人数已满），回滚：删除刚插入的报名记录，保证计数与报名记录一致
            activitySignupDao.deleteDocument(signup.getSignupId());
            throw new BizException("报名人数已满");
        }
        log.info("活动报名成功，activityId={}，userId={}，signupId={}", activityId, userId, signup.getSignupId());
        return signup;
    }

    /**
     * 获取已加入的邻居列表
     *
     * @param activityId 活动ID
     * @return 报名记录列表（含昵称、头像、加入时间）
     */
    public List<ActivitySignup> getJoinedNeighbors(Long activityId) {
        return activitySignupDao.findDocumentList(
                Criteria.where("activityId").is(activityId),
                Sort.Order.desc("joinTime"));
    }

    /**
     * 我的活动列表（发布/参与）
     * 通过 role 区分：published=我发布的, joined=我参与的。
     * TODO: 当前 joined 查询先取出用户全部报名记录的 activityId 再分页查询活动，
     * 如果用户参与活动量大，建议改为 aggregate $lookup 分页查询以提升性能。
     *
     * @param userId  当前用户ID
     * @param queryVO 查询入参
     * @return 分页活动列表（含 statusText）
     */
    public Page<Activity> getMyActivities(Long userId, MyActivityQueryVO queryVO) {
        Pageable pageable = PageRequest.of(queryVO.getPageNumber(), queryVO.getPageSize(),
                Sort.by(Sort.Order.desc("createTime")));
        String role = StrUtil.isBlank(queryVO.getRole()) ? "published" : queryVO.getRole();
        Page<Activity> page;
        if ("joined".equals(role)) {
            // 我参与的：先查询用户的报名记录，获取活动ID列表，再分页查询活动
            List<Long> activityIds = activitySignupDao.findDistinct(
                    Criteria.where("userId").is(userId), "activityId");
            if (CollectionUtil.isEmpty(activityIds)) {
                // 无报名记录，返回空页
                page = new PageImpl<>(Collections.emptyList(), pageable, 0);
            } else {
                Criteria criteria = Criteria.where("activityId").in(activityIds);
                page = activityDao.findDocumentPage(criteria, pageable);
            }
        } else {
            // 我发布的
            Criteria criteria = Criteria.where("publisherUserId").is(userId);
            // [新增 2026-08-03 17:30] 社区数据隔离
            if (queryVO.getCommunityId() != null) {
                criteria.and("communityId").is(queryVO.getCommunityId());
            }
            page = activityDao.findDocumentPage(criteria, pageable);
        }
        // 填充状态文本
        for (Activity activity : page.getContent()) {
            populateStatus(activity);
        }
        return page;
    }

    /**
     * 批量更新活动状态（可由定时任务调用）
     * 根据当前时间和活动的 startTime/endTime 刷新持久化的 status 字段。
     */
    public void updateActivityStatus() {
        Date now = new Date();
        // 1. 未开始：当前时间 < startTime 且状态不为1
        activityDao.updateMulti(
                Criteria.where("startTime").gt(now).and("status").ne(ActivityStatusEnum.NOT_STARTED.getCode()),
                new Update().set("status", ActivityStatusEnum.NOT_STARTED.getCode()));
        // 2. 进行中：startTime <= 当前时间 <= endTime 且状态不为2
        activityDao.updateMulti(
                Criteria.where("startTime").lte(now).and("endTime").gte(now)
                        .and("status").ne(ActivityStatusEnum.IN_PROGRESS.getCode()),
                new Update().set("status", ActivityStatusEnum.IN_PROGRESS.getCode()));
        // 3. 已结束：当前时间 > endTime 且状态不为3
        activityDao.updateMulti(
                Criteria.where("endTime").lt(now).and("status").ne(ActivityStatusEnum.ENDED.getCode()),
                new Update().set("status", ActivityStatusEnum.ENDED.getCode()));
        log.info("批量更新活动状态完成");
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 校验创建活动入参
     */
    private void validateCreateVO(ActivityCreateVO vo) {
        if (StrUtil.isBlank(vo.getTitle())) {
            throw new BizException("活动标题不能为空");
        }
        if (vo.getStartTime() == null || vo.getEndTime() == null) {
            throw new BizException("活动开始时间和结束时间不能为空");
        }
        if (!vo.getEndTime().after(vo.getStartTime())) {
            throw new BizException("活动结束时间必须晚于开始时间");
        }
        if (vo.getType() != null && ActivityTypeEnum.getByCode(vo.getType()) == null) {
            throw new BizException("无效的活动类型");
        }
        // 人数限制不能为负数（0 表示不限）
        if (vo.getMaxLimit() != null && vo.getMaxLimit() < 0) {
            throw new BizException("人数限制不能为负数");
        }
    }

    /**
     * 构建排序条件
     * sort=new 按创建时间排序，sort=use/user 按参与人数排序
     */
    private Sort buildSort(String sortStr) {
        if ("use".equals(sortStr) || "user".equals(sortStr)) {
            return Sort.by(Sort.Order.desc("participantCount"));
        }
        // 默认按创建时间排序
        return Sort.by(Sort.Order.desc("createTime"));
    }

    /**
     * 应用参与人数范围筛选
     * range 格式：0-50 / 50-100 / 100+
     */
    private void applyRangeFilter(Criteria criteria, String range) {
        if (StrUtil.isBlank(range)) {
            return;
        }
        switch (range) {
            case "0-50":
                criteria.and("participantCount").gte(0).lte(50);
                break;
            case "50-100":
                criteria.and("participantCount").gte(50).lte(100);
                break;
            case "100+":
                criteria.and("participantCount").gte(100);
                break;
            default:
                break;
        }
    }

    /**
     * 填充活动的实时状态和状态文本
     */
    private void populateStatus(Activity activity) {
        ActivityStatusEnum statusEnum = ActivityStatusEnum.computeStatus(activity.getStartTime(), activity.getEndTime());
        activity.setStatus(statusEnum.getCode());
        activity.setStatusText(statusEnum.getDesc());
    }

    /**
     * 批量填充发布者昵称和头像
     * 收集所有 publisherUserId，一次性查询用户信息后映射填充。
     */
    private void populateAuthorInfo(List<Activity> activities) {
        if (CollectionUtil.isEmpty(activities)) {
            return;
        }
        // 收集所有发布者ID（去重）
        Set<Long> publisherIds = activities.stream()
                .map(Activity::getPublisherUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (publisherIds.isEmpty()) {
            return;
        }
        // 批量查询用户信息
        List<User> users = userDao.findDocumentList(Criteria.where("userId").in(publisherIds));
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getUserId, u -> u, (a, b) -> a));
        // 填充发布者信息
        for (Activity activity : activities) {
            User user = userMap.get(activity.getPublisherUserId());
            if (user != null) {
                activity.setAuthorName(user.getNickName());
                activity.setAuthorAvatar(user.getAvatar());
            }
        }
    }

}
