package com.demo.weixin.service.store;

import com.demo.common.exception.BizException;
import com.demo.weixin.dao.store.PointsRecordDao;
import com.demo.weixin.dao.store.UserPointsDao;
import com.demo.weixin.entity.store.PointsRecord;
import com.demo.weixin.entity.store.UserPoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * 用户积分服务
 * 独立管理用户积分账户，支持扣减、返还、增加操作。
 * 使用 MongoDB 原子操作（$inc）保证并发安全。
 * [修改 2026-08-03 19:10] 新增积分流水记录功能，每次变动自动写入pointsRecord集合
 */
@Service
@Slf4j
public class UserPointsService {

    @Autowired
    private UserPointsDao userPointsDao;
    // [新增 2026-08-03 19:10] 积分流水记录DAO
    @Autowired
    private PointsRecordDao pointsRecordDao;

    // [新增 2026-08-03 19:10] 积分变动类型常量
    private static final int TYPE_EARN = 1;    // 获得
    private static final int TYPE_SPEND = 2;   // 消耗
    private static final int TYPE_REFUND = 3;  // 退还

    /**
     * 获取或创建用户积分账户
     * 首次访问时自动创建，初始余额为0
     */
    public UserPoints getOrCreate(Long userId) {
        UserPoints points = userPointsDao.findOne(Criteria.where("userId").is(userId));
        if (points == null) {
            points = new UserPoints();
            points.setUserId(userId);
            points.setBalance(0);
            points.setFrozenBalance(0);
            points.setTotalEarned(0);
            points.setTotalSpent(0);
            userPointsDao.insertDocument(points);
            log.info("创建用户积分账户，userId={}", userId);
        }
        return points;
    }

    // [删除 2026-07-31 14:43] 原 getBalance(Long userId) 方法已删除
    // 原因：与 getOrCreate 返回的 UserPoints.getBalance() 功能重复，调用方统一改用 getOrCreate().getBalance()

    /**
     * 扣减积分（下单支付时调用）
     * 使用条件更新保证并发安全：只有余额>=扣减量时才成功
     *
     * @param userId 用户ID
     * @param amount 扣减数量（正数）
     */
    public void deduct(Long userId, int amount) {
        // [修改 2026-08-03 19:10] 委托给带来源参数的重载方法
        deduct(userId, amount, "商城消费", null);
    }

    /**
     * 扣减积分（带来源信息）
     * [新增 2026-08-03 19:10] 支持记录变动来源和关联订单
     *
     * @param userId          用户ID
     * @param amount          扣减数量（正数）
     * @param source          变动来源描述
     * @param relatedOrderId  关联订单ID（可为null）
     */
    public void deduct(Long userId, int amount, String source, Long relatedOrderId) {
        if (amount <= 0) {
            throw new BizException("扣减积分数量必须大于0");
        }
        UserPoints points = getOrCreate(userId);
        if (points.getBalance() < amount) {
            throw new BizException("积分余额不足，当前余额：" + points.getBalance() + "，需要：" + amount);
        }
        // 条件更新：balance >= amount 才扣减，防止并发超扣
        Criteria criteria = Criteria.where("userPointsId").is(points.getUserPointsId())
                .and("balance").gte(amount);
        Update update = new Update()
                .inc("balance", -amount)
                .inc("totalSpent", amount);
        Boolean success = userPointsDao.updateOneDocument(criteria, update);
        if (!success) {
            throw new BizException("积分扣减失败，可能余额已变化，请重试");
        }
        int balanceAfter = points.getBalance() - amount;
        log.info("扣减用户积分，userId={}，amount={}，扣减后余额={}", userId, amount, balanceAfter);
        // [新增 2026-08-03 19:10] 记录积分流水
        recordTransaction(userId, TYPE_SPEND, amount, balanceAfter, source, relatedOrderId, null);
    }

    /**
     * 返还积分（退款时调用）
     *
     * @param userId 用户ID
     * @param amount 返还数量（正数）
     */
    public void refund(Long userId, int amount) {
        // [修改 2026-08-03 19:10] 委托给带来源参数的重载方法
        refund(userId, amount, "退款返还", null);
    }

    /**
     * 返还积分（带来源信息）
     * [新增 2026-08-03 19:10] 支持记录变动来源和关联订单
     *
     * @param userId          用户ID
     * @param amount          返还数量（正数）
     * @param source          变动来源描述
     * @param relatedOrderId  关联订单ID（可为null）
     */
    public void refund(Long userId, int amount, String source, Long relatedOrderId) {
        if (amount <= 0) {
            return;
        }
        UserPoints points = getOrCreate(userId);
        Criteria criteria = Criteria.where("userPointsId").is(points.getUserPointsId());
        Update update = new Update()
                .inc("balance", amount)
                .inc("totalSpent", -amount);
        userPointsDao.updateOneDocument(criteria, update);
        int balanceAfter = points.getBalance() + amount;
        log.info("返还用户积分，userId={}，amount={}，返还后余额={}", userId, amount, balanceAfter);
        // [新增 2026-08-03 19:10] 记录积分流水
        recordTransaction(userId, TYPE_REFUND, amount, balanceAfter, source, relatedOrderId, null);
    }

    /**
     * 增加积分（签到/管理员操作/活动奖励等）
     *
     * @param userId 用户ID
     * @param amount 增加数量（正数）
     */
    public void add(Long userId, int amount) {
        // [修改 2026-08-03 19:10] 委托给带来源参数的重载方法
        add(userId, amount, "系统奖励", null);
    }

    /**
     * 增加积分（带来源信息）
     * [新增 2026-08-03 19:10] 支持记录变动来源和关联订单
     *
     * @param userId          用户ID
     * @param amount          增加数量（正数）
     * @param source          变动来源描述
     * @param relatedOrderId  关联订单ID（可为null）
     */
    public void add(Long userId, int amount, String source, Long relatedOrderId) {
        if (amount <= 0) {
            return;
        }
        UserPoints points = getOrCreate(userId);
        Criteria criteria = Criteria.where("userPointsId").is(points.getUserPointsId());
        Update update = new Update()
                .inc("balance", amount)
                .inc("totalEarned", amount);
        userPointsDao.updateOneDocument(criteria, update);
        int balanceAfter = points.getBalance() + amount;
        log.info("增加用户积分，userId={}，amount={}，增加后余额={}", userId, amount, balanceAfter);
        // [新增 2026-08-03 19:10] 记录积分流水
        recordTransaction(userId, TYPE_EARN, amount, balanceAfter, source, relatedOrderId, null);
    }

    /**
     * 记录积分流水
     * [新增 2026-08-03 19:10] 私有方法，在每次积分变动后调用
     * 流水记录失败不影响主业务流程（try-catch兜底）
     *
     * @param userId          用户ID
     * @param type            变动类型：1=获得，2=消耗，3=退还
     * @param amount          变动数量（正数）
     * @param balanceAfter    变动后余额
     * @param source          变动来源
     * @param relatedOrderId  关联订单ID
     * @param remark          备注
     */
    private void recordTransaction(Long userId, int type, int amount, int balanceAfter,
                                   String source, Long relatedOrderId, String remark) {
        try {
            PointsRecord record = new PointsRecord();
            record.setUserId(userId);
            record.setType(type);
            record.setAmount(amount);
            record.setBalanceAfter(balanceAfter);
            record.setSource(source);
            record.setRelatedOrderId(relatedOrderId);
            record.setRemark(remark);
            pointsRecordDao.insertDocument(record);
        } catch (Exception e) {
            // 流水记录失败不影响积分操作主流程
            log.warn("记录积分流水失败，userId={}，type={}，amount={}", userId, type, amount, e);
        }
    }
}
