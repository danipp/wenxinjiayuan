package com.demo.weixin.vo.demo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模拟支付回调入参（调试用）
 */
@Data
@Schema(description = "模拟支付回调入参")
public class SimulatePayVO {

    @Schema(description = "订单号")
    private String orderNum;

    @Schema(description = "是否支付成功（默认true）")
    private Boolean success;
}
