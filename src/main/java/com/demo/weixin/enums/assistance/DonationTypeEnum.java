package com.demo.weixin.enums.assistance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 捐赠类型枚举
 */
@Getter
@Schema(description = "捐赠类型枚举")
public enum DonationTypeEnum {

    MONEY("money", "资金捐赠"),   // 资金捐赠
    GOODS("goods", "物资捐赠");   // 物资捐赠

    private final String code;
    private final String desc;

    DonationTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static DonationTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (DonationTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
