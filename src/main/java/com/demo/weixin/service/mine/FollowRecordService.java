package com.demo.weixin.service.mine;

import com.demo.common.exception.BizException;
import com.demo.weixin.dao.mine.FollowRecordDao;
import com.demo.weixin.entity.mine.FollowRecord;
import com.demo.weixin.service.store.StoreShopService;
import com.demo.weixin.service.store.UserPointsService;
import com.demo.weixin.vo.mine.FollowVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

/**
 * 关注记录服务
 * 提供关注记录分页查询、关注和取消关注操作。
 * 关注操作需要原子性检查：不能重复关注，关注成功后给被关注者增加10积分。
 * 取消关注时删除关注记录，并尝试扣减被关注者相应积分；积分不足时跳过扣减（已消费的积分无法强制扣回），
 * 保证用户始终能取消关注。
 */
@Service
@Slf4j
public class FollowRecordService {

    /** 关注奖励积分数量 */
    private static final int FOLLOW_REWARD_POINTS = 10;

    @Autowired
    private FollowRecordDao followRecordDao;
    @Autowired
    private UserPointsService userPointsService;
    // [新增 2026-07-31 17:21] 注入店铺服务，关注/取消关注时同步更新店铺冗余统计字段
    @Autowired
    private StoreShopService storeShopService;

    /**
     * 分页查询关注记录（查询当前用户关注了谁，即"我的关注"列表）
     *
     * @param userId  当前用户ID（关注者）
     * @param pageNumber 页码
     * @param pageSize  每页条数
     * @return 关注记录分页结果
     */
    public Page<FollowRecord> getFollowPage(Long userId, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Order.desc("createTime")));
        // 查询当前用户作为关注者的记录（我关注了谁），供前端"我的关注"列表使用
        Criteria criteria = Criteria.where("followerUserId").is(userId);
        return followRecordDao.findDocumentPage(criteria, pageable);
    }

    /**
     * 关注操作
     * 原子性检查：不能重复关注，关注成功后给被关注者（targetUserId）增加10积分。
     * 注意：分布式幂等锁在 Controller 层通过 @DistributedIdempotent 保证防重入，
     * 此处通过数据库唯一性检查防止重复关注。
     *
     * @param targetUserId  被关注者用户ID
     * @param followerUserId 关注者用户ID
     * @param vo            关注者信息（姓名、电话、头像，冗余存储）
     * @return 创建后的关注记录
     */
    public FollowRecord follow(Long targetUserId, Long followerUserId, FollowVO vo) {
        if (targetUserId == null) {
            throw new BizException("被关注者ID不能为空");
        }
        if (targetUserId.equals(followerUserId)) {
            throw new BizException("不能关注自己");
        }
        // 检查是否已关注（防止重复关注）
        FollowRecord existing = followRecordDao.findOne(
                Criteria.where("targetUserId").is(targetUserId)
                        .and("followerUserId").is(followerUserId));
        if (existing != null) {
            throw new BizException("已关注该用户，请勿重复关注");
        }
        // 创建关注记录
        FollowRecord record = new FollowRecord();
        record.setTargetUserId(targetUserId);
        record.setFollowerUserId(followerUserId);
        if (vo != null) {
            record.setFollowerName(vo.getFollowerName());
            record.setFollowerPhone(vo.getFollowerPhone());
            record.setFollowerAvatar(vo.getFollowerAvatar());
        }
        // 插入关注记录：check-then-insert 非原子，由唯一索引兜底；并发场景下冲突则返回已有记录
        try {
            followRecordDao.insertDocument(record);
        } catch (DuplicateKeyException e) {
            // 唯一索引冲突，说明在并发检查与插入之间已被其他请求抢先关注
            log.warn("关注记录唯一索引冲突，返回已有记录，targetUserId={}，followerUserId={}", targetUserId, followerUserId);
            return followRecordDao.findOne(
                    Criteria.where("targetUserId").is(targetUserId)
                            .and("followerUserId").is(followerUserId));
        }
        log.info("关注成功，followId={}，targetUserId={}，followerUserId={}",
                record.getFollowId(), targetUserId, followerUserId);
        // 给被关注者增加积分
        userPointsService.add(targetUserId, FOLLOW_REWARD_POINTS, "关注奖励", null);
        log.info("被关注者获得积分奖励，targetUserId={}，points={}", targetUserId, FOLLOW_REWARD_POINTS);
        // [新增 2026-07-31 17:21] 同步更新店铺冗余统计字段
        // 关注者（followerUserId）的店铺 followCount +1
        storeShopService.updateFollowCount(followerUserId, 1);
        // 被关注者（targetUserId）的店铺 fansCount +1（因关注=收藏，粉丝数=收藏数）
        storeShopService.updateFansCount(targetUserId, 1);
        return record;
    }

    /**
     * 取消关注
     * 删除关注记录，同时尝试扣减被关注者积分（与关注时奖励的积分对等）。
     * 已消费的积分无法强制扣回，允许取消关注但积分不扣减：积分不足时跳过扣减，保证用户始终能取消关注。
     *
     * @param targetUserId  被关注者用户ID
     * @param followerUserId 关注者用户ID
     */
    public void unfollow(Long targetUserId, Long followerUserId) {
        if (targetUserId == null) {
            throw new BizException("被关注者ID不能为空");
        }
        // 不能取关自己
        if (targetUserId.equals(followerUserId)) {
            throw new BizException("不能取关自己");
        }
        // 查找关注记录（targetUserId + followerUserId），不存在则抛异常
        Criteria criteria = Criteria.where("targetUserId").is(targetUserId)
                .and("followerUserId").is(followerUserId);
        FollowRecord record = followRecordDao.findOne(criteria);
        if (record == null) {
            throw new BizException("未关注该用户");
        }
        // 已消费的积分无法强制扣回，允许取消关注但积分不扣减
        try {
            userPointsService.deduct(targetUserId, FOLLOW_REWARD_POINTS, "取消关注扣减", null);
            log.info("取消关注扣减被关注者积分，targetUserId={}，deductAmount={}", targetUserId, FOLLOW_REWARD_POINTS);
        } catch (BizException e) {
            // 积分不足时跳过扣减，保证用户始终能取消关注
            log.warn("取消关注时被关注者积分不足，跳过扣减", e);
        }
        // 无论积分是否扣减成功，都删除关注记录
        // 使用物理删除而非逻辑删除：因 FollowRecord 上有 (targetUserId, followerUserId, del_flag) 唯一索引，
        // 逻辑删除会留下 del_flag=true 的记录，导致用户重新关注后再次取关时唯一索引冲突。
        followRecordDao.deleteDocumentPhisiclly(criteria);
        log.info("取消关注成功，targetUserId={}，followerUserId={}", targetUserId, followerUserId);
        // [新增 2026-07-31 17:21] 同步更新店铺冗余统计字段
        // 关注者（followerUserId）的店铺 followCount -1
        storeShopService.updateFollowCount(followerUserId, -1);
        // 被关注者（targetUserId）的店铺 fansCount -1
        storeShopService.updateFansCount(targetUserId, -1);
    }
}
