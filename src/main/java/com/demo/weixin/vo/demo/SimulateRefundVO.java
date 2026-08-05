package com.demo.weixin.vo.demo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模拟退款回调入参（调试用）
 */
@Data
@Schema(description = "模拟退款回调入参")
public class SimulateRefundVO {

    @Schema(description = "订单号")
    private String orderNum;

    @Schema(description = "是否退款成功（默认true）")
    private Boolean success;
}
