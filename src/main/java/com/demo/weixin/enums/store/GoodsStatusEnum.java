package com.demo.weixin.enums.store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 商品上下架状态枚举
 */
@Getter
@Schema(description = "商品状态枚举")
public enum GoodsStatusEnum {

    ON_SALE(1, "上架中"),    // 商品可被浏览和购买
    OFF_SHELF(2, "已下架");  // 商品不可购买，仅管理员/卖家可见

    private final int code;
    private final String desc;

    GoodsStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static GoodsStatusEnum getByCode(int code) {
        for (GoodsStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
