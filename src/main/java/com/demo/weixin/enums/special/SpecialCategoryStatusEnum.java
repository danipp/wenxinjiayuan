package com.demo.weixin.enums.special;

import lombok.Getter;

/**
 * 社区特惠分类状态枚举
 */
@Getter
public enum SpecialCategoryStatusEnum {

    ENABLED(1, "启用"),
    DISABLED(2, "停用"),
    ;

    private final int code;
    private final String desc;

    SpecialCategoryStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
