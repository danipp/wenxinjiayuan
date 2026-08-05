package com.demo.weixin.enums.special;

import lombok.Getter;

/**
 * 优惠券领券记录状态枚举
 */
@Getter
public enum CouponRecordStatusEnum {

    UNUSED(1, "未使用"),
    USED(2, "已使用"),
    EXPIRED(3, "已过期"),
    ;

    private final int code;
    private final String desc;

    CouponRecordStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
