package com.demo.weixin.job;

import com.demo.weixin.service.activity.ActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 活动状态自动刷新定时任务
 * [新增 2026-08-03 18:50] 定期批量刷新活动持久化状态字段
 * <p>
 * 根据活动的 startTime/endTime 自动将状态流转为：
 * - 未开始(1)：当前时间 < startTime
 * - 进行中(2)：startTime <= 当前时间 <= endTime
 * - 已结束(3)：当前时间 > endTime
 * </p>
 * <p>
 * 设计说明：
 * 1. 使用 fixedDelay 而非 fixedRate，保证两次执行之间有明确间隔，防止任务堆积
 * 2. 仅更新状态不匹配的活动记录（status != 目标状态），减少无效写入
 * 3. 异常不影响后续执行，下次定时触发会自动重试
 * </p>
 */
@Component
@Slf4j
public class ActivityStatusTask {

    @Autowired
    private ActivityService activityService;

    /**
     * 每5分钟刷新一次活动状态
     * fixedDelay：从上次任务结束时间算起，等待5分钟后再执行下一次
     */
    @Scheduled(fixedDelay = 300000)
    public void refreshActivityStatus() {
        try {
            long start = System.currentTimeMillis();
            activityService.updateActivityStatus();
            long cost = System.currentTimeMillis() - start;
            log.info("活动状态刷新完成，耗时 {}ms", cost);
        } catch (Exception e) {
            log.error("活动状态刷新任务异常", e);
        }
    }
}
