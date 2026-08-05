package com.demo.weixin.entity.store;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商城商品实体
 * goodsType 字段表示支付方式（1积分 2现金 3混合），scene 字段表示商品场景/商城归属。
 * [变更 2026-07-31 18:04] 拆分 goodsType 和 scene 两个维度，goodsType 只管支付方式，scene 管商品场景。
 * 原 goodsType=4（打卡相框）已废弃，相框通过 scene=frame 标识，支付方式由 goodsType 决定。
 */
@Data
@NoArgsConstructor
@Document(collection = "storeGoods")
@Schema(description = "商城商品")
public class StoreGoods extends Base {

    /** 商品业务主键 */
    @Field
    private Long goodsId;

    /** 所属店铺ID（null表示社区直发商品） */
    @Field
    @Schema(description = "所属店铺ID")
    private Long shopId;

    /** 商品标题 */
    @Field
    @Schema(description = "商品标题")
    private String title;

    /** 商品详情描述 */
    @Field
    @Schema(description = "商品详情描述")
    private String description;

    /** 封面图URL */
    @Field
    @Schema(description = "封面图URL")
    private String coverImage;

    /** 轮播图URL列表 */
    @Field
    @Schema(description = "轮播图URL列表")
    private List<String> carouselImages;

    /** 积分价格（兑换所需积分数量，0表示不支持积分兑换） */
    @Field
    @Schema(description = "积分价格")
    private Integer pointsPrice;

    /** 现金价格（购买所需金额，0表示不支持现金购买） */
    @Field
    @Schema(description = "现金价格")
    private BigDecimal cashPrice;

    /** 原价/参考价（用于显示划线价） */
    @Field
    @Schema(description = "原价/参考价")
    private BigDecimal originalPrice;

    /** 库存数量 */
    @Field
    @Schema(description = "库存数量")
    private Integer stock;

    /** 销量 */
    @Field
    @Schema(description = "销量")
    private Integer salesCount;

    // [变更 2026-07-31 18:04] goodsType 只表示支付方式，不再包含打卡相框
    /** 商品类型（支付方式）：1积分兑换 2现金购买 3混合（积分+现金） */
    @Field
    @Schema(description = "商品类型（支付方式）：1积分兑换 2现金购买 3混合")
    private Integer goodsType;

    // [新增 2026-07-31 18:04] 商品场景字段，与 goodsType 正交，表示商品所属的商城场景
    /** 商品场景：volunteer志愿者商城 points积分兑换 assistance消费帮扶 frame打卡相框 */
    @Field
    @Schema(description = "商品场景：volunteer志愿者商城 points积分兑换 assistance消费帮扶 frame打卡相框")
    private String scene;

    /** 商品状态：1上架中 2已下架 */
    @Field
    @Schema(description = "商品状态：1上架中 2已下架")
    private Integer status;

    /** 商品分类 */
    @Field
    @Schema(description = "商品分类")
    private String category;

    /** 规格参数（JSON字符串，如 {"材质":"棉","尺寸":"L"} ） */
    @Field
    @Schema(description = "规格参数JSON")
    private String specs;

    // ==================== 相框场景专属字段（scene=frame 时使用） ====================

    /** 相框编号（如 FRAME-NFC-001） */
    @Field
    @Schema(description = "相框编号")
    private String frameNo;

    /** 规格尺寸（如 6寸、7寸、8寸） */
    @Field
    @Schema(description = "规格尺寸")
    private String frameSize;

    // [变更 2026-07-31 18:04] 原 scene 字段改名为 sceneDesc，避免与商品场景 scene 字段冲突
    /** 适用场景描述（如 社区活动、长者探访、志愿服务，scene=frame时使用） */
    @Field
    @Schema(description = "适用场景描述（相框场景专属）")
    private String sceneDesc;

    /** 配送方式（如 社区配送、快递配送） */
    @Field
    @Schema(description = "配送方式")
    private String delivery;

    /** 功能特性列表（如 ["NFC碰一碰","快速打卡","活动记录"] ） */
    @Field
    @Schema(description = "功能特性列表")
    private List<String> features;

    // [新增 2026-08-03 17:10] 社区数据隔离字段
    /** 所属社区ID（用于数据隔离） */
    @Field
    @Schema(description = "所属社区ID")
    private Long communityId;

    /** 所属社区名称（冗余字段） */
    @Field
    @Schema(description = "所属社区名称")
    private String communityName;

    @Override
    public Long getID() {
        return goodsId;
    }

    @Override
    public void setID(Long id) {
        this.goodsId = id;
    }
}
