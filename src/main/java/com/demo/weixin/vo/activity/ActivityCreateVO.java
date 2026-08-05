package com.demo.weixin.vo.activity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * 创建活动入参
 */
@Data
@Schema(description = "创建活动入参")
public class ActivityCreateVO {

    @Schema(description = "活动标题")
    @NotBlank(message = "活动标题不能为空")
    private String title;

    @Schema(description = "活动内容/描述")
    private String content;

    @Schema(description = "活动地点")
    private String location;

    @Schema(description = "活动开始时间")
    @NotNull(message = "活动开始时间不能为空")
    private Date startTime;

    @Schema(description = "活动结束时间")
    @NotNull(message = "活动结束时间不能为空")
    private Date endTime;

    @Schema(description = "所属社区")
    private String community;

    // [新增 2026-08-03 17:20] 社区数据隔离字段
    @Schema(description = "所属社区ID")
    private Long communityId;

    @Schema(description = "人数限制（0表示不限）")
    private Integer maxLimit;

    @Schema(description = "是否收集手机号")
    private Boolean collectPhone;

    @Schema(description = "活动类型：1线上活动 2线下活动 3招募活动")
    @NotNull(message = "活动类型不能为空")
    private Integer type;

    @Schema(description = "封面图URL")
    private String coverImage;

    @Schema(description = "活动标签")
    private String tag;
}
