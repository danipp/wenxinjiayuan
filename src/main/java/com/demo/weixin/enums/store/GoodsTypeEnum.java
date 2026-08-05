package com.demo.weixin.enums.store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 商品类型枚举（支付方式）
 * [变更 2026-07-31 18:04] 去掉 FRAME(4)，打卡相框改为通过 GoodsSceneEnum.FRAME 标识。
 * goodsType 只表示支付方式，与商品场景（scene）正交。
 */
@Getter
@Schema(description = "商品类型枚举（支付方式）")
public enum GoodsTypeEnum {

    POINTS(1, "积分兑换"),    // 纯积分支付
    CASH(2, "现金购买"),      // 微信现金支付
    MIXED(3, "混合支付");     // 积分+现金混合支付

    private final int code;
    private final String desc;

    GoodsTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据类型码获取枚举
     */
    public static GoodsTypeEnum getByCode(int code) {
        for (GoodsTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
