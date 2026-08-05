package com.demo.weixin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 志愿者录入入参（管理员使用）
 * [新增 2026-08-03 21:00] 管理员手动录入志愿者ID，可同时录入手机号用于后续微信授权关联
 */
@Data
@Schema(description = "志愿者录入入参")
public class VolunteerImportVO {

    @Schema(description = "志愿者ID（唯一，管理员手动录入或第三方平台返回）")
    @NotBlank(message = "志愿者ID不能为空")
    private String volunteerId;

    @Schema(description = "志愿者姓名/昵称")
    private String nickName;

    @Schema(description = "手机号（可选，用于微信授权登录时自动关联志愿者身份）")
    private String cellphone;

    @Schema(description = "所属社区ID（可选）")
    private Long communityId;

    @Schema(description = "所属社区名称（可选，冗余字段）")
    private String communityName;

    @Schema(description = "志愿者ID（编辑时必传，新增时不传）")
    private Long userId;
}
