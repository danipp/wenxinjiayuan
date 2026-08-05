package com.demo.weixin.vo.store;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单核销入参
 * 卖家输入核销码验证并完成订单。
 */
@Data
@Schema(description = "订单核销入参")
public class OrderVerifyVO {

    @Schema(description = "订单ID")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "核销码（8位数字）")
    @NotBlank(message = "核销码不能为空")
    private String redeemCode;
}
