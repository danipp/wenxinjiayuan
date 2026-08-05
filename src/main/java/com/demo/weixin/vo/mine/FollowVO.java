package com.demo.weixin.vo.mine;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 关注操作入参
 */
@Data
@Schema(description = "关注操作入参")
public class FollowVO {

    @Schema(description = "被关注者用户ID")
    @NotNull(message = "被关注者用户ID不能为空")
    private Long targetUserId;

    @Schema(description = "关注者姓名（可选，冗余存储）")
    private String followerName;

    @Schema(description = "关注者电话（可选，冗余存储）")
    private String followerPhone;

    @Schema(description = "关注者头像URL（可选，冗余存储）")
    private String followerAvatar;
}
