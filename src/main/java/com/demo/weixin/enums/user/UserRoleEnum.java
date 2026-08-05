package com.demo.weixin.enums.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 用户角色枚举
 * [新增 2026-08-03 21:00] 区分普通居民和志愿者两种角色
 * 普通居民通过微信手机号授权登录，志愿者可通过手机号授权或志愿者ID登录
 */
@Getter
@Schema(description = "用户角色枚举")
public enum UserRoleEnum {

    RESIDENT(1, "居民"),
    VOLUNTEER(2, "志愿者");

    private final Integer code;
    private final String desc;

    UserRoleEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UserRoleEnum getByCode(int code) {
        for (UserRoleEnum role : values()) {
            if (role.code == code) {
                return role;
            }
        }
        return null;
    }
}
