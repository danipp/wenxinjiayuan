package com.demo.weixin.enums.store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 支付类型枚举
 * [变更 2026-07-31 18:04] 新增 MIXED(3) 混合支付类型，支持积分+现金同时支付。
 */
@Getter
@Schema(description = "支付类型枚举")
public enum PayTypeEnum {

    POINTS(1, "积分兑换"),   // 纯积分支付，下单即扣减积分
    CASH(2, "现金购买"),     // 微信现金支付，走支付回调
    MIXED(3, "混合支付");    // 积分+现金混合支付，先扣积分再发起微信支付

    private final int code;
    private final String desc;

    PayTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static PayTypeEnum getByCode(int code) {
        for (PayTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
