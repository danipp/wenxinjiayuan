package com.demo.weixin.enums.store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 订单财务支付状态枚举
 * 与 OrderStatusEnum（业务状态）分离，独立跟踪财务支付生命周期。
 * 业务状态管订单流转（待支付→待核销→已完成），支付状态管资金流向（待付款→已付款→退款中→已退款）。
 */
@Getter
@Schema(description = "订单支付状态枚举")
public enum PaymentStatusEnum {

    PENDING_PAY("PENDING_PAY", "待付款"),     // 订单已创建，等待支付
    PAID("PAID", "已付款"),                   // 支付成功（微信回调确认）
    REFUND_APPLY("REFUND_APPLY", "申请退款中"), // 买家发起退款申请
    REFUNDING("REFUNDING", "退款处理中"),       // 卖家同意退款，微信退款处理中
    REFUNDED("REFUNDED", "已退款"),            // 退款到账完成
    CLOSED("CLOSED", "已关闭");               // 超时未支付自动关闭或手动关闭

    private final String code;
    private final String desc;

    PaymentStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static PaymentStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (PaymentStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
