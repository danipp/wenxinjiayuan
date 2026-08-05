package com.demo.weixin.vo.store;

import com.demo.weixin.vo.BaseQueryVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品分页查询入参
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品分页查询入参")
public class GoodsQueryVO extends BaseQueryVo {

    @Schema(description = "所属店铺ID")
    private Long shopId;

    @Schema(description = "商品分类")
    private String category;

    @Schema(description = "商品类型（支付方式）：1积分兑换 2现金购买 3混合")
    private Integer goodsType;

    // [新增 2026-07-31 18:04] 商品场景筛选，对应前端商城 tab
    @Schema(description = "商品场景：volunteer志愿者商城 points积分兑换 assistance消费帮扶 frame打卡相框")
    private String scene;

    @Schema(description = "商品状态：1上架中 2已下架")
    private Integer status;

    @Schema(description = "关键词（模糊搜索商品标题）")
    private String keyword;

    // [新增 2026-08-03 17:20] 社区数据隔离字段
    @Schema(description = "所属社区ID（数据隔离用，前端传入当前选中社区ID）")
    private Long communityId;
}
