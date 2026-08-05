package com.demo.weixin.vo.mine;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 紧急联系人添加/编辑入参
 * contactId 为空时新增，非空时更新。
 */
@Data
@Schema(description = "紧急联系人添加/编辑入参")
public class EmergencyContactVO {

    @Schema(description = "联系人ID（为空时新增，非空时编辑）")
    private Long contactId;

    @Schema(description = "联系人姓名")
    @NotBlank(message = "联系人姓名不能为空")
    private String name;

    @Schema(description = "联系人电话")
    @NotBlank(message = "联系人电话不能为空")
    private String phone;

    @Schema(description = "关系（如：父母、子女、配偶、朋友等）")
    private String relation;
}
