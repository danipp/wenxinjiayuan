package com.demo.weixin.enums.assistance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 物资申领状态枚举
 */
@Getter
@Schema(description = "物资申领状态枚举")
public enum ClaimStatusEnum {

    PENDING("pending", "待审核"),           // 用户已提交申领，等待审核
    APPROVED("approved", "已通过"),         // 审核通过，待发放
    REJECTED("rejected", "已拒绝"),         // 审核未通过
    DISTRIBUTED("distributed", "已发放");   // 物资已发放给申领人

    private final String code;
    private final String desc;

    ClaimStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static ClaimStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (ClaimStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
