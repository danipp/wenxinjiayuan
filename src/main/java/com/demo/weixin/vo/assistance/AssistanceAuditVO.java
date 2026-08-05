package com.demo.weixin.vo.assistance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 帮扶审核入参
 */
@Data
@Schema(description = "帮扶审核入参")
public class AssistanceAuditVO {

    @Schema(description = "帮扶申请ID")
    @NotNull(message = "帮扶申请ID不能为空")
    private Long applyId;

    @Schema(description = "是否通过")
    @NotNull(message = "是否通过不能为空")
    private Boolean approved;

    @Schema(description = "审核备注")
    private String auditRemark;
}
