package com.demo.weixin.enums.assistance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 捐赠申请状态枚举
 */
@Getter
@Schema(description = "捐赠申请状态枚举")
public enum DonationStatusEnum {

    PENDING("pending", "待审核"),     // 用户已提交，等待审核
    APPROVED("approved", "已通过"),   // 审核通过
    REJECTED("rejected", "已拒绝"),   // 审核未通过
    COMPLETED("completed", "已完成"); // 捐赠已完成

    private final String code;
    private final String desc;

    DonationStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static DonationStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (DonationStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
