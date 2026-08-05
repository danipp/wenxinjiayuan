package com.demo.weixin.service.activity;

import cn.hutool.core.collection.CollectionUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.dao.UserDao;
import com.demo.weixin.dao.activity.ActivityDao;
import com.demo.weixin.dao.activity.ActivityPhotoDao;
import com.demo.weixin.dao.activity.ActivitySignupDao;
import com.demo.weixin.entity.User;
import com.demo.weixin.entity.activity.Activity;
import com.demo.weixin.entity.activity.ActivityPhoto;
import com.demo.weixin.entity.activity.ActivitySignup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动照片服务
 * 管理活动照片的上传、查询和点赞功能。
 * 点赞使用 likedUserIds 列表记录点赞用户，toggleLike 通过 $addToSet/$pull 原子操作保证并发安全。
 */
@Service
@Slf4j
public class ActivityPhotoService {

    @Autowired
    private ActivityPhotoDao activityPhotoDao;
    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private ActivitySignupDao activitySignupDao;
    @Autowired
    private UserDao userDao;

    /**
     * 上传活动照片
     * 图片已通过OSS上传，本接口仅记录照片信息。
     * 校验：活动是否存在、当前用户是否报名参与过该活动。
     *
     * @param userId    上传用户ID
     * @param activityId 活动ID
     * @param imageUrl   图片URL
     * @return 创建的照片记录
     */
    public ActivityPhoto uploadPhoto(Long userId, Long activityId, String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new BizException("图片URL不能为空");
        }
        // 1. 校验活动是否存在
        Activity activity = activityDao.findById(activityId);
        if (activity == null) {
            throw new BizException("活动不存在");
        }
        // 2. 校验当前用户是否报名参与过该活动（只有参与过的用户才能上传照片）
        ActivitySignup signup = activitySignupDao.findOne(
                Criteria.where("activityId").is(activityId).and("userId").is(userId));
        if (signup == null) {
            throw new BizException("只有参与过该活动的用户才能上传照片");
        }
        // 3. 获取用户信息（冗余到照片记录）
        User user = userDao.findById(userId);
        // 4. 创建照片记录
        ActivityPhoto photo = new ActivityPhoto();
        photo.setActivityId(activityId);
        photo.setUserId(userId);
        photo.setNickName(user != null ? user.getNickName() : "匿名用户");
        photo.setImageUrl(imageUrl);
        photo.setLikes(0);
        photo.setLikedUserIds(new ArrayList<>());
        activityPhotoDao.insertDocument(photo);
        log.info("上传活动照片，photoId={}，activityId={}，userId={}", photo.getPhotoId(), activityId, userId);
        return photo;
    }

    /**
     * 获取活动照片列表
     * 填充当前用户的点赞状态（isLiked）。
     *
     * @param activityId    活动ID
     * @param currentUserId 当前登录用户ID（可能为null，未登录时 isLiked 全为 false）
     * @return 照片列表
     */
    public List<ActivityPhoto> getPhotoList(Long activityId, Long currentUserId) {
        List<ActivityPhoto> photos = activityPhotoDao.findDocumentList(
                Criteria.where("activityId").is(activityId),
                Sort.Order.desc("createTime"));
        // 填充当前用户的点赞状态
        for (ActivityPhoto photo : photos) {
            if (currentUserId != null && CollectionUtil.isNotEmpty(photo.getLikedUserIds())) {
                photo.setIsLiked(photo.getLikedUserIds().contains(currentUserId));
            } else {
                photo.setIsLiked(false);
            }
        }
        return photos;
    }

    /**
     * 点赞/取消点赞（需要并发控制）
     * 通过检查 likedUserIds 判断当前状态，使用 $addToSet/$pull + $inc 原子操作更新。
     * 关键：取消点赞时仅在 userId 仍在 likedUserIds 中时才执行 pull+inc(-1)，
     * 点赞时仅在 userId 不在 likedUserIds 中时才执行 addToSet+inc(+1)，
     * 避免 pull/addToSet 未命中但 inc 仍执行导致 likes 计数与 likedUserIds 不一致。
     *
     * @param photoId 照片ID
     * @param userId  当前用户ID
     * @return true=点赞成功，false=取消点赞成功
     */
    public Boolean toggleLike(Long photoId, Long userId) {
        ActivityPhoto photo = activityPhotoDao.findById(photoId);
        if (photo == null) {
            throw new BizException("照片不存在");
        }
        // 判断当前是否已点赞
        boolean alreadyLiked = CollectionUtil.isNotEmpty(photo.getLikedUserIds())
                && photo.getLikedUserIds().contains(userId);
        if (alreadyLiked) {
            // 取消点赞：仅当 userId 仍在 likedUserIds 中时才执行 pull + inc(-1)，
            // 避免并发场景下 pull 未命中但 inc(-1) 仍执行导致 likes 变为负数
            Boolean updated = activityPhotoDao.updateOneDocument(
                    Criteria.where("photoId").is(photoId).and("likedUserIds").is(userId),
                    new Update().pull("likedUserIds", userId).inc("likes", -1));
            if (!updated) {
                // 未命中（已被并发取消或从未点赞），返回当前状态：未点赞
                log.info("取消点赞未命中，photoId={}，userId={}，当前状态为未点赞", photoId, userId);
                return false;
            }
            log.info("取消点赞，photoId={}，userId={}", photoId, userId);
            return false;
        } else {
            // 点赞：仅当 userId 不在 likedUserIds 中时才执行 addToSet + inc(+1)，
            // 避免并发场景下重复点赞导致 likes 多计
            Boolean updated = activityPhotoDao.updateOneDocument(
                    Criteria.where("photoId").is(photoId).and("likedUserIds").ne(userId),
                    new Update().addToSet("likedUserIds", userId).inc("likes", 1));
            if (!updated) {
                // 未命中（已被并发点赞），返回当前状态：已点赞
                log.info("点赞未命中，photoId={}，userId={}，当前状态为已点赞", photoId, userId);
                return true;
            }
            log.info("点赞照片，photoId={}，userId={}", photoId, userId);
            return true;
        }
    }
}
