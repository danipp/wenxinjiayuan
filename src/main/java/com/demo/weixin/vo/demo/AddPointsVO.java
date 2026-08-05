package com.demo.weixin.vo.demo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 积分充值入参（调试用）
 */
@Data
@Schema(description = "积分充值入参")
public class AddPointsVO {

    @Schema(description = "充值积分数量（默认1000）")
    private Integer points;
}
