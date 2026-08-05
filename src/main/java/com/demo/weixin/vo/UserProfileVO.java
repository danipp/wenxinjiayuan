package com.demo.weixin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户资料更新入参
 * 用于更新昵称、头像、个人描述等信息（头像图片已通过OSS上传，此处仅传URL）。
 */
@Data
@Schema(description = "用户资料更新入参")
public class UserProfileVO {

    /** 昵称 */
    @Schema(description = "昵称")
    private String nickName;

    /** 头像URL */
    @Schema(description = "头像URL")
    private String avatar;

    /** 个人描述 */
    @Schema(description = "个人描述")
    private String description;
}
