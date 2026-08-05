package com.demo.weixin.enums.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 通知类型枚举
 * [新增 2026-08-03 19:30] 用于区分首页消息轮播的通知类型
 */
@Getter
@Schema(description = "通知类型枚举")
public enum NoticeTypeEnum {

    SYSTEM(1, "系统公告"),
    ACTIVITY(2, "社区活动"),
    DONATION(3, "捐赠播报"),
    ASSISTANCE(4, "帮扶动态");

    private final int code;
    private final String desc;

    NoticeTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static NoticeTypeEnum getByCode(int code) {
        for (NoticeTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
