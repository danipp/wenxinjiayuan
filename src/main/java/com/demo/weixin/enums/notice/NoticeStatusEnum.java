package com.demo.weixin.enums.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 通知状态枚举
 * [新增 2026-08-03 19:30]
 */
@Getter
@Schema(description = "通知状态枚举")
public enum NoticeStatusEnum {

    PUBLISHED(1, "上架"),
    UNPUBLISHED(2, "下架");

    private final Integer code;
    private final String desc;

    NoticeStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static NoticeStatusEnum getByCode(int code) {
        for (NoticeStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
