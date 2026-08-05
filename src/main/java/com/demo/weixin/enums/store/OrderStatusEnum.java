package com.demo.weixin.enums.store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 商城订单状态枚举
 * 集中定义订单生命周期中的所有合法状态，配合 OrderStateMachine 统一管理状态流转。
 * 状态码使用10的倍数递增，便于后续插入中间状态。
 */
@Getter
@Schema(description = "订单状态枚举")
public enum OrderStatusEnum {

    PENDING_PAY(10, "待支付"),           // 订单已创建，等待支付（现金支付场景）
    PENDING_VERIFY(20, "待核销"),        // 已支付，等待卖家核销兑换码
    COMPLETED(30, "已完成"),             // 卖家核销完成，交易达成
    REFUND_REQUESTED(40, "退款申请中"),   // 买家发起退款申请，等待卖家处理
    REFUND_APPROVED(41, "退款已通过"),    // 卖家同意退款，退款处理中
    REFUND_REJECTED(42, "退款被拒绝"),    // 卖家拒绝退款，订单恢复待核销
    REFUNDED(50, "已退款"),              // 退款处理完成，积分/现金已退回
    CANCELLED(60, "已取消");             // 买家在支付前取消订单

    private final int code;
    private final String desc;

    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static OrderStatusEnum getByCode(int code) {
        for (OrderStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
