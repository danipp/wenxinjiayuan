package com.demo.weixin.enums.mine;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 居民认证状态枚举
 */
@Getter
@Schema(description = "认证状态枚举")
public enum CertificationStatusEnum {

    PENDING("PENDING", "待审核"),     // 用户已提交，等待管理员审核
    APPROVED("APPROVED", "已通过"),   // 审核通过，用户已认证
    REJECTED("REJECTED", "已拒绝");   // 审核未通过

    private final String code;
    private final String desc;

    CertificationStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static CertificationStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (CertificationStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
