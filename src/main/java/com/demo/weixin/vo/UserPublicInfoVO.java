package com.demo.weixin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户公开信息VO
 * [新增 2026-08-03 19:00] 仅包含可公开的用户信息，不暴露手机号、openId等敏感字段
 */
@Data
@Schema(name = "用户公开信息")
public class UserPublicInfoVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "个人简介")
    private String description;

    @Schema(description = "所属社区ID")
    private Long communityId;

    @Schema(description = "所属社区名称")
    private String communityName;
}
