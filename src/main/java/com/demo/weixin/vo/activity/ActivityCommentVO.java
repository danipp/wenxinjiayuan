package com.demo.weixin.vo.activity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 写活动评价入参
 */
@Data
@Schema(description = "写活动评价入参")
public class ActivityCommentVO {

    @Schema(description = "活动ID")
    @NotNull(message = "活动ID不能为空")
    private Long activityId;

    @Schema(description = "评分（1-5星）")
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1")
    @Max(value = 5, message = "评分最高为5")
    private Integer score;

    @Schema(description = "评价表情（如emoji表情符号）")
    private String emoji;

    @Schema(description = "状态标签文本（如\"非常满意\"、\"值得推荐\"）")
    private String statusText;

    @Schema(description = "评价内容")
    private String content;
}
