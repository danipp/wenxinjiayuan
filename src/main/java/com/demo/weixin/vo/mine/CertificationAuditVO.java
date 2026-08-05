package com.demo.weixin.vo.mine;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 居民认证审核入参（管理员使用）
 */
@Data
@Schema(description = "居民认证审核入参")
public class CertificationAuditVO {

    @Schema(description = "认证记录ID")
    @NotNull(message = "认证记录ID不能为空")
    private Long certificationId;

    @Schema(description = "审核结果：APPROVED(通过) REJECTED(拒绝)")
    @NotBlank(message = "审核结果不能为空")
    private String status;

    @Schema(description = "审核备注（拒绝原因等）")
    private String auditRemark;
}
