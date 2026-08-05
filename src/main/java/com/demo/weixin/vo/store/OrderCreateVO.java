package com.demo.weixin.vo.store;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单创建入参
 * 买家选择商品后下单，支付类型决定走积分扣减还是现金支付流程。
 */
@Data
@Schema(description = "订单创建入参")
public class OrderCreateVO {

    @Schema(description = "商品ID")
    @NotNull(message = "商品ID不能为空")
    private Long goodsId;

    @Schema(description = "购买数量（默认1）")
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量必须大于0")
    private Integer count;

    @Schema(description = "支付类型：1积分兑换 2现金购买")
    @NotNull(message = "支付类型不能为空")
    private Integer payType;
}
