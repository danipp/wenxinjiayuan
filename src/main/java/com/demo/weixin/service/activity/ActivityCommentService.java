package com.demo.weixin.service.activity;

import cn.hutool.core.util.StrUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.UserDao;
import com.demo.weixin.dao.activity.ActivityCommentDao;
import com.demo.weixin.dao.activity.ActivityDao;
import com.demo.weixin.dao.activity.ActivitySignupDao;
import com.demo.weixin.entity.User;
import com.demo.weixin.entity.activity.Activity;
import com.demo.weixin.entity.activity.ActivityComment;
import com.demo.weixin.entity.activity.ActivitySignup;
import com.demo.weixin.enums.activity.ActivityStatusEnum;
import com.demo.weixin.vo.activity.ActivityCommentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

/**
 * 活动评价服务
 * 用户在活动结束后对活动进行评价，包含评分、表情、状态标签和评价内容。
 * 一个用户对同一活动只能评价一次。
 */
@Service
@Slf4j
public class ActivityCommentService {

    @Autowired
    private ActivityCommentDao activityCommentDao;
    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private ActivitySignupDao activitySignupDao;
    @Autowired
    private UserDao userDao;

    /**
     * 写活动评价
     * 校验：活动是否存在、活动是否已结束、用户是否报名参与过、用户是否已评价过、评分范围。
     *
     * @param userId 评价用户ID
     * @param vo     评价入参
     * @return 创建的评价
     */
    public ActivityComment createComment(Long userId, ActivityCommentVO vo) {
        // 1. 校验活动是否存在
        Activity activity = activityDao.findById(vo.getActivityId());
        if (activity == null) {
            throw new BizException("活动不存在");
        }
        // 2. 校验活动是否已结束
        ActivityStatusEnum currentStatus = ActivityStatusEnum.computeStatus(activity.getStartTime(), activity.getEndTime());
        if (currentStatus != ActivityStatusEnum.ENDED) {
            throw new BizException("活动尚未结束，无法评价");
        }
        // 3. 校验当前用户是否报名参与过该活动（只有参与过的用户才能评价）
        ActivitySignup signup = activitySignupDao.findOne(
                Criteria.where("activityId").is(vo.getActivityId()).and("userId").is(userId));
        if (signup == null) {
            throw new BizException("只有参与过该活动的用户才能评价");
        }
        // 4. 校验是否已评价过
        ActivityComment existComment = activityCommentDao.findOne(
                Criteria.where("activityId").is(vo.getActivityId())
                        .and("userId").is(userId));
        if (existComment != null) {
            throw new BizException("您已评价过该活动，不能重复评价");
        }
        // 5. 校验评分范围
        if (vo.getScore() == null || vo.getScore() < 1 || vo.getScore() > 5) {
            throw new BizException("评分必须在1-5之间");
        }
        if (StrUtil.isBlank(vo.getContent())) {
            throw new BizException("评价内容不能为空");
        }
        // 6. 获取用户信息（冗余到评价记录）
        User user = userDao.findById(userId);
        // 7. 创建评价
        ActivityComment comment = new ActivityComment();
        comment.setActivityId(vo.getActivityId());
        comment.setUserId(userId);
        comment.setNickName(user != null ? user.getNickName() : "匿名用户");
        comment.setAvatar(user != null ? user.getAvatar() : null);
        comment.setScore(vo.getScore());
        comment.setEmoji(vo.getEmoji());
        comment.setStatusText(vo.getStatusText());
        comment.setContent(vo.getContent());
        activityCommentDao.insertDocument(comment);
        log.info("创建活动评价，commentId={}，activityId={}，score={}", comment.getCommentId(), vo.getActivityId(), vo.getScore());
        return comment;
    }

    /**
     * 评价分页查询
     *
     * @param activityId 活动ID
     * @param pageable   分页参数
     * @return 评价分页列表
     */
    public Page<ActivityComment> getCommentPage(Long activityId, Pageable pageable) {
        Criteria criteria = Criteria.where("activityId").is(activityId);
        return activityCommentDao.findDocumentPage(criteria, pageable);
    }

    /**
     * 计算活动平均评分
     * 通过求和评分字段除以评价数量计算平均值。
     *
     * @param activityId 活动ID
     * @return 平均评分（无评价时返回0.0）
     */
    public Double getAverageScore(Long activityId) {
        Criteria criteria = Criteria.where("activityId").is(activityId);
        long count = activityCommentDao.count(criteria);
        if (count == 0) {
            return 0.0;
        }
        Object sum = activityCommentDao.sumByField(criteria, "score");
        double total = sum instanceof Number ? ((Number) sum).doubleValue() : 0.0;
        return total / count;
    }
}
