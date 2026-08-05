package com.demo.weixin.enums.assistance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 爱心企业状态枚举
 */
@Getter
@Schema(description = "爱心企业状态枚举")
public enum EnterpriseStatusEnum {

    ACTIVE("active", "已上线"),     // 企业已上线，展示在爱心企业列表
    INACTIVE("inactive", "已下线"); // 企业已下线，不在列表展示

    private final String code;
    private final String desc;

    EnterpriseStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static EnterpriseStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (EnterpriseStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
