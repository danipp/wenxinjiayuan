package com.demo.weixin.vo.mine;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 需求评价入参
 * 发布者对帮忙者的服务进行评价，评价后需求状态从待评价流转为已完成。
 */
@Data
@Schema(description = "需求评价入参")
public class DemandEvaluateVO {

    @Schema(description = "需求ID")
    @NotNull(message = "需求ID不能为空")
    private Long demandId;

    @Schema(description = "评分（1-5）")
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1")
    @Max(value = 5, message = "评分最高为5")
    private Integer rating;

    @Schema(description = "评价内容")
    private String content;
}
