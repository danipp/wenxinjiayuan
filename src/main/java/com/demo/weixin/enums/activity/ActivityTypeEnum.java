package com.demo.weixin.enums.activity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 活动类型枚举
 */
@Getter
@Schema(description = "活动类型枚举")
public enum ActivityTypeEnum {

    ONLINE(1, "线上活动"),       // 线上活动
    OFFLINE(2, "线下活动"),      // 线下活动
    RECRUITMENT(3, "招募活动");  // 志愿者招募活动（复用Activity结构，通过type筛选）

    private final int code;
    private final String desc;

    ActivityTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据类型码获取枚举
     */
    public static ActivityTypeEnum getByCode(int code) {
        for (ActivityTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
