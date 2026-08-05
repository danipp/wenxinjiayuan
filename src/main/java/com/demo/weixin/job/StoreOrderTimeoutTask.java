package com.demo.weixin.job;

import com.demo.weixin.entity.store.StoreOrder;
import com.demo.weixin.service.store.StoreOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 商城订单超时自动关闭定时任务
 * <p>
 * 每5分钟扫描一次待支付订单中已超过expireTime（创建时设定的30分钟）的现金订单，
 * 自动关闭并恢复库存，避免库存被长期占用。
 * </p>
 * <p>
 * 设计说明：
 * 1. 仅处理 status=PENDING_PAY 且 expireTime <= now 的订单
 * 2. 积分支付订单在创建时同步完成支付，不会进入 PENDING_PAY 状态，因此不会被扫描
 * 3. 关闭操作使用条件更新（status必须仍为PENDING_PAY），保证原子性
 * 4. 单条订单关闭失败不影响其他订单处理
 * </p>
 */
@Component
@Slf4j
public class StoreOrderTimeoutTask {

    @Autowired
    private StoreOrderService storeOrderService;

    /**
     * 每5分钟扫描超时未支付订单并自动关闭
     * fixedDelay：从上次任务结束时间算起，等待5分钟后再执行下一次
     * 相比 fixedRate（从开始时间算起），fixedDelay 能保证两次执行之间有明确间隔，
     * 即使某次扫描耗时较长也不会导致任务堆积或重叠执行
     */
    @Scheduled(fixedDelay = 300000)
    public void closeTimeoutOrders() {
        try {
            List<StoreOrder> expiredOrders = storeOrderService.findExpiredOrders();
            if (expiredOrders == null || expiredOrders.isEmpty()) {
                return;
            }
            log.info("扫描到超时未支付订单 {} 笔，开始自动关闭", expiredOrders.size());
            int closedCount = 0;
            int skippedCount = 0;
            for (StoreOrder order : expiredOrders) {
                try {
                    boolean closed = storeOrderService.closeExpiredOrder(order);
                    if (closed) {
                        closedCount++;
                    } else {
                        skippedCount++;
                    }
                } catch (Exception e) {
                    // 单条订单关闭失败不影响其他订单
                    log.error("关闭超时订单异常，orderId={}，orderNum={}",
                            order.getOrderId(), order.getOrderNum(), e);
                }
            }
            log.info("超时订单处理完成，成功关闭 {} 笔，跳过 {} 笔，共 {} 笔",
                    closedCount, skippedCount, expiredOrders.size());
        } catch (Exception e) {
            log.error("超时订单扫描任务异常", e);
        }
    }
}
