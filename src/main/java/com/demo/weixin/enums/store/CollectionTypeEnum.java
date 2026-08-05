package com.demo.weixin.enums.store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 收藏类型枚举
 * 区分商品收藏与店铺收藏，复用同一张收藏表。
 */
@Getter
@Schema(description = "收藏类型枚举")
public enum CollectionTypeEnum {

    GOODS(1, "商品收藏"),  // targetId 对应 StoreGoods.goodsId
    SHOP(2, "店铺收藏");   // targetId 对应 StoreShop.shopId

    private final int code;
    private final String desc;

    CollectionTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static CollectionTypeEnum getByCode(int code) {
        for (CollectionTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
