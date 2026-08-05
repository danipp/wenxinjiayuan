package com.demo.weixin.enums.store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 商城订单事件枚举
 * 定义触发订单状态流转的业务事件，与 OrderStateMachine 配合使用。
 * 所有状态变更必须通过事件触发，禁止在业务代码中直接修改订单状态字段。
 */
@Getter
@Schema(description = "订单事件枚举")
public enum OrderEventEnum {

    PAY_SUCCESS("支付成功"),        // 支付回调成功，待支付→待核销
    VERIFY("核销完成"),            // 卖家核销兑换码，待核销→已完成
    REQUEST_REFUND("申请退款"),     // 买家申请退款，待核销→退款申请中
    APPROVE_REFUND("同意退款"),     // 卖家同意退款，退款申请中→退款已通过
    REJECT_REFUND("拒绝退款"),     // 卖家拒绝退款，退款申请中→退款被拒绝
    RESTORE("恢复订单"),           // 拒绝退款后恢复，退款被拒绝→待核销
    REFUND_COMPLETE("退款完成"),    // 退款处理完成，退款已通过→已退款
    CANCEL("取消订单");            // 买家取消，待支付→已取消

    private final String desc;

    OrderEventEnum(String desc) {
        this.desc = desc;
    }
}
