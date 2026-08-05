package com.demo.weixin.service.store;

import com.demo.common.exception.BizException;
import com.demo.weixin.enums.store.OrderEventEnum;
import com.demo.weixin.enums.store.OrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 商城订单状态机（集中式管理）
 * <p>
 * 所有订单状态流转必须经过此状态机，禁止在业务代码中直接修改订单状态。
 * 状态转换规则在此处统一维护，确保状态流转的合法性和可追溯性。
 * </p>
 *
 * 合法状态流转图：
 * <pre>
 *   待支付(PENDING_PAY) ──支付成功──> 待核销(PENDING_VERIFY)
 *   待支付(PENDING_PAY) ──取消──────> 已取消(CANCELLED)
 *   待核销(PENDING_VERIFY) ──核销──────────> 已完成(COMPLETED)
 *   待核销(PENDING_VERIFY) ──申请退款──────> 退款申请中(REFUND_REQUESTED)
 *   退款申请中(REFUND_REQUESTED) ──同意退款──> 退款已通过(REFUND_APPROVED)
 *   退款申请中(REFUND_REQUESTED) ──拒绝退款──> 退款被拒绝(REFUND_REJECTED)
 *   退款被拒绝(REFUND_REJECTED) ──恢复──────> 待核销(PENDING_VERIFY)
 *   退款已通过(REFUND_APPROVED) ──退款完成──> 已退款(REFUNDED)
 * </pre>
 */
@Component
@Slf4j
public class OrderStateMachine {

    /**
     * 状态转换规则表：key = "当前状态码:事件名"，value = 目标状态
     * 全局唯一的状态流转定义，所有Service必须通过此Map校验和执行状态变更。
     */
    private static final Map<String, OrderStatusEnum> TRANSITIONS = new HashMap<>();

    static {
        // 待支付 -> 待核销（支付成功）
        TRANSITIONS.put(transKey(OrderStatusEnum.PENDING_PAY, OrderEventEnum.PAY_SUCCESS), OrderStatusEnum.PENDING_VERIFY);
        // 待支付 -> 已取消（买家取消）
        TRANSITIONS.put(transKey(OrderStatusEnum.PENDING_PAY, OrderEventEnum.CANCEL), OrderStatusEnum.CANCELLED);
        // 待核销 -> 已完成（卖家核销）
        TRANSITIONS.put(transKey(OrderStatusEnum.PENDING_VERIFY, OrderEventEnum.VERIFY), OrderStatusEnum.COMPLETED);
        // 待核销 -> 退款申请中（买家申请退款）
        TRANSITIONS.put(transKey(OrderStatusEnum.PENDING_VERIFY, OrderEventEnum.REQUEST_REFUND), OrderStatusEnum.REFUND_REQUESTED);
        // 退款申请中 -> 退款已通过（卖家同意退款）
        TRANSITIONS.put(transKey(OrderStatusEnum.REFUND_REQUESTED, OrderEventEnum.APPROVE_REFUND), OrderStatusEnum.REFUND_APPROVED);
        // 退款申请中 -> 退款被拒绝（卖家拒绝退款）
        TRANSITIONS.put(transKey(OrderStatusEnum.REFUND_REQUESTED, OrderEventEnum.REJECT_REFUND), OrderStatusEnum.REFUND_REJECTED);
        // 退款被拒绝 -> 待核销（自动恢复）
        TRANSITIONS.put(transKey(OrderStatusEnum.REFUND_REJECTED, OrderEventEnum.RESTORE), OrderStatusEnum.PENDING_VERIFY);
        // 退款已通过 -> 已退款（退款处理完成）
        TRANSITIONS.put(transKey(OrderStatusEnum.REFUND_APPROVED, OrderEventEnum.REFUND_COMPLETE), OrderStatusEnum.REFUNDED);
    }

    /**
     * 生成状态转换规则的key
     */
    private static String transKey(OrderStatusEnum status, OrderEventEnum event) {
        return status.getCode() + ":" + event.name();
    }

    /**
     * 执行状态流转，返回新的订单状态。
     * 如果当前状态不支持该事件，抛出BizException。
     *
     * @param currentStatus 当前订单状态
     * @param event         触发事件
     * @return 流转后的新状态
     */
    public OrderStatusEnum transit(OrderStatusEnum currentStatus, OrderEventEnum event) {
        // L4: 防止OrderStatusEnum.getByCode返回null导致NPE
        if (currentStatus == null) {
            throw new BizException("订单状态异常");
        }
        OrderStatusEnum newStatus = TRANSITIONS.get(transKey(currentStatus, event));
        if (newStatus == null) {
            log.warn("非法的订单状态流转：当前状态[{}]不支持操作[{}]", currentStatus.getDesc(), event.getDesc());
            throw new BizException("当前订单状态[" + currentStatus.getDesc() + "]不支持该操作[" + event.getDesc() + "]");
        }
        log.info("订单状态流转：{} --[{}]--> {}", currentStatus.getDesc(), event.getDesc(), newStatus.getDesc());
        return newStatus;
    }

    /**
     * 校验当前状态是否允许执行指定事件，不允许则抛出异常。
     *
     * @param currentStatus 当前订单状态
     * @param event         待校验事件
     */
    public void checkTransition(OrderStatusEnum currentStatus, OrderEventEnum event) {
        if (!TRANSITIONS.containsKey(transKey(currentStatus, event))) {
            throw new BizException("当前订单状态[" + currentStatus.getDesc() + "]不支持该操作[" + event.getDesc() + "]");
        }
    }

    /**
     * 判断当前状态是否允许执行指定事件。
     *
     * @param currentStatus 当前订单状态
     * @param event         待判断事件
     * @return true=允许，false=不允许
     */
    public boolean canTransit(OrderStatusEnum currentStatus, OrderEventEnum event) {
        return TRANSITIONS.containsKey(transKey(currentStatus, event));
    }
}
