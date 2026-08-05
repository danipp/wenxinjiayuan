package com.demo.weixin.vo.mine;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 居民认证提交入参
 * 用户通过微信授权手机号后，补充认证信息提交审核。
 */
@Data
@Schema(description = "居民认证提交入参")
public class ResidentCertificationVO {

    @Schema(description = "手机号（微信授权获取）")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @Schema(description = "社区名称")
    @NotBlank(message = "社区名称不能为空")
    private String communityName;

    @Schema(description = "真实姓名")
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @Schema(description = "身份证号")
    @NotBlank(message = "身份证号不能为空")
    private String idCard;

    @Schema(description = "居住地址")
    private String address;
}
