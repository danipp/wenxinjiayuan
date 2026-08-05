package com.demo.weixin.vo.assistance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 物资申领审核入参
 */
@Data
@Schema(description = "物资申领审核入参")
public class ClaimAuditVO {

    @Schema(description = "申领ID")
    @NotNull(message = "申领ID不能为空")
    private Long claimId;

    @Schema(description = "是否通过")
    @NotNull(message = "是否通过不能为空")
    private Boolean approved;

    @Schema(description = "审核备注")
    private String auditRemark;
}
