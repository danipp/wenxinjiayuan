package com.demo.weixin.vo.demo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模拟认证审核入参（调试用）
 */
@Data
@Schema(description = "模拟认证审核入参")
public class SimulateAuditVO {

    @Schema(description = "是否审核通过（默认true）")
    private Boolean approved;
}
