package com.demo.weixin.vo.activity;

import com.demo.weixin.vo.BaseQueryVo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 活动广场查询入参
 * 支持按排序方式、参与人数范围、活动类型筛选。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "活动广场查询入参")
public class ActivityQueryVO extends BaseQueryVo {

    @Schema(description = "排序方式：new按创建时间排序, use按参与人数排序, user按参与人数排序")
    private String sort;

    @Schema(description = "参与人数范围筛选：0-50/50-100/100+")
    private String range;

    @Schema(description = "活动类型筛选：1线上活动 2线下活动")
    private Integer type;

    /**
     * 页码（从1开始）。
     * 此处重新声明并加 @Min 校验，避免修改公共类 BaseQueryVo；
     * 默认值与 BaseQueryVo 保持一致。
     */
    @Schema(description = "页码，默认为1")
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNumber = 1;

    /**
     * 单页数据量。
     * 此处重新声明并加 @Min 校验，避免修改公共类 BaseQueryVo；
     * 默认值与 BaseQueryVo 保持一致。建议前端限制不超过100。
     */
    @Schema(description = "单页数据量，默认为20")
    @Min(value = 1, message = "单页数据量最小为1")
    private Integer pageSize = 20;

    // [新增 2026-08-03 17:20] 社区数据隔离字段
    @Schema(description = "所属社区ID（数据隔离用，前端传入当前选中社区ID）")
    private Long communityId;
}
