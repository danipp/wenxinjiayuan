package com.demo.weixin.vo.store;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单退款申请入参
 * 买家在订单待核销状态下发起退款申请。
 */
@Data
@Schema(description = "订单退款申请入参")
public class OrderRefundVO {

    @Schema(description = "订单ID")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "退款原因")
    private String reason;
}
