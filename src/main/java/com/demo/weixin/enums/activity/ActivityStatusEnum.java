package com.demo.weixin.enums.activity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 活动状态枚举
 * 活动状态根据 startTime/endTime 自动计算：
 * 当前时间 < startTime 为未开始，startTime <= 当前时间 <= endTime 为进行中，当前时间 > endTime 为已结束。
 * TODO: 业务上可能需要"已取消"状态（如发布者主动取消活动），当前暂未支持，待确认业务需求后补充。
 */
@Getter
@Schema(description = "活动状态枚举")
public enum ActivityStatusEnum {

    NOT_STARTED(1, "未开始"),   // 活动尚未开始
    IN_PROGRESS(2, "进行中"),   // 活动正在进行
    ENDED(3, "已结束");         // 活动已结束

    private final int code;
    private final String desc;

    ActivityStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static ActivityStatusEnum getByCode(int code) {
        for (ActivityStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据开始时间和结束时间自动计算活动状态
     *
     * @param startTime 活动开始时间
     * @param endTime   活动结束时间
     * @return 对应的活动状态枚举
     */
    public static ActivityStatusEnum computeStatus(java.util.Date startTime, java.util.Date endTime) {
        // 开始或结束时间为空时，默认按未开始处理，避免空指针
        if (startTime == null || endTime == null) {
            return NOT_STARTED;
        }
        java.util.Date now = new java.util.Date();
        if (now.before(startTime)) {
            return NOT_STARTED;
        }
        if (now.after(endTime)) {
            return ENDED;
        }
        return IN_PROGRESS;
    }
}
