package com.demo.weixin.enums.mine;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 需求/帮忙状态枚举
 * 集中定义需求发布与帮忙记录共享的状态流转。
 * 需求发布记录和帮忙记录共用 DemandRecord 表，通过 role 区分视角。
 */
@Getter
@Schema(description = "需求/帮忙状态枚举")
public enum DemandStatusEnum {

    PENDING(1, "待帮忙"),      // 需求已发布，等待帮忙者接单
    HELPING(2, "已接单"),       // 帮忙者已接单，服务进行中
    TO_EVALUATE(3, "待评价"),   // 服务已完成，等待发布者评价
    COMPLETED(4, "已完成"),     // 评价完成，需求闭环
    EXPIRED(5, "已过期");       // 超过有效期限未被接单，自动过期

    private final int code;
    private final String desc;

    DemandStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static DemandStatusEnum getByCode(int code) {
        for (DemandStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
