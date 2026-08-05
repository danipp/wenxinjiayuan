package com.demo.weixin.enums.store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 商品场景枚举
 * [新增 2026-07-31 18:04] 表示商品所属的商城场景，与 goodsType（支付方式）正交。
 * 前端商城 tab 与 scene 的对应关系：
 * - volunteer（志愿者商城）：积分+现金混合定价商品为主
 * - points（积分兑换）：纯积分兑换商品
 * - assistance（消费帮扶）：助农扶贫专项商品
 * - frame（打卡相框）：NFC打卡相框商品，带相框专属字段
 */
@Getter
@Schema(description = "商品场景枚举")
public enum GoodsSceneEnum {

    VOLUNTEER("volunteer", "志愿者商城"),
    POINTS("points", "积分兑换"),
    ASSISTANCE("assistance", "消费帮扶"),
    FRAME("frame", "打卡相框");

    private final String code;
    private final String desc;

    GoodsSceneEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据场景码获取枚举
     */
    public static GoodsSceneEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (GoodsSceneEnum scene : values()) {
            if (scene.code.equals(code)) {
                return scene;
            }
        }
        return null;
    }
}
