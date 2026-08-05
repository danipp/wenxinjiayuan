package com.demo.weixin.vo.store;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品创建/编辑入参
 * [变更 2026-07-31 18:04] goodsType 只表示支付方式（1积分 2现金 3混合），新增 scene 表示商品场景。
 * 相框专属字段由 scene=frame 判断写入，不再由 goodsType=4 判断。
 */
@Data
@Schema(description = "商品创建编辑入参")
public class GoodsCreateVO {

    @Schema(description = "商品ID（编辑时传入，新增时不传）")
    private Long goodsId;

    @Schema(description = "所属店铺ID")
    @NotNull(message = "店铺ID不能为空")
    private Long shopId;

    @Schema(description = "商品标题")
    @NotBlank(message = "商品标题不能为空")
    private String title;

    @Schema(description = "商品详情描述")
    private String description;

    @Schema(description = "封面图URL")
    private String coverImage;

    @Schema(description = "轮播图URL列表")
    private List<String> carouselImages;

    @Schema(description = "积分价格")
    private Integer pointsPrice;

    @Schema(description = "现金价格")
    private BigDecimal cashPrice;

    @Schema(description = "原价/参考价")
    private BigDecimal originalPrice;

    @Schema(description = "库存数量")
    private Integer stock;

    // [变更 2026-07-31 18:04] goodsType 只表示支付方式
    @Schema(description = "商品类型（支付方式）：1积分兑换 2现金购买 3混合")
    @NotNull(message = "商品类型不能为空")
    private Integer goodsType;

    // [新增 2026-07-31 18:04] 商品场景字段
    @Schema(description = "商品场景：volunteer志愿者商城 points积分兑换 assistance消费帮扶 frame打卡相框")
    @NotBlank(message = "商品场景不能为空")
    private String scene;

    @Schema(description = "商品分类")
    private String category;

    @Schema(description = "规格参数JSON")
    private String specs;

    // ==================== 相框场景专属字段（scene=frame 时使用） ====================

    @Schema(description = "相框编号（如 FRAME-NFC-001，scene=frame时必填）")
    private String frameNo;

    @Schema(description = "规格尺寸（如 6寸、7寸、8寸，scene=frame时必填）")
    private String frameSize;

    @Schema(description = "适用场景（如 社区活动、长者探访、志愿服务，scene=frame时使用）")
    private String sceneDesc;

    @Schema(description = "配送方式（如 社区配送、快递配送，scene=frame时使用）")
    private String delivery;

    @Schema(description = "功能特性列表（如 [\"NFC碰一碰\",\"快速打卡\",\"活动记录\"]，scene=frame时使用）")
    private List<String> features;
}
