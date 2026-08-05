package com.demo.weixin.enums.special;

import lombok.Getter;

/**
 * 优惠券状态枚举
 */
@Getter
public enum CouponStatusEnum {

    ACTIVE(1, "进行中"),
    EXPIRED(2, "已过期"),
    OFFLINE(3, "已下架"),
    ;

    private final int code;
    private final String desc;

    CouponStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
