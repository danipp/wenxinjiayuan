package com.demo.weixin.enums.community;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 社区状态枚举
 */
@Getter
@AllArgsConstructor
public enum CommunityStatusEnum {

    /** 启用 */
    ACTIVE(1, "启用"),
    /** 禁用 */
    INACTIVE(2, "禁用");

    private final Integer code;
    private final String desc;

    /**
     * 根据code获取枚举
     */
    public static CommunityStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CommunityStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
