package com.demo.weixin.enums.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 志愿者状态枚举
 * [新增 2026-08-03 21:00] 管理员录入志愿者后默认为正常状态，可停用/启用
 */
@Getter
@Schema(description = "志愿者状态枚举")
public enum VolunteerStatusEnum {

    INACTIVE(0, "未激活"),
    ACTIVE(1, "正常"),
    DISABLED(2, "停用");

    private final Integer code;
    private final String desc;

    VolunteerStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static VolunteerStatusEnum getByCode(int code) {
        for (VolunteerStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
