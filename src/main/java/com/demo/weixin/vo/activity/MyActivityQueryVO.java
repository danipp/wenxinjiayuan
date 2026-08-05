package com.demo.weixin.vo.activity;

import com.demo.weixin.vo.BaseQueryVo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 我的活动查询入参
 * 通过 role 区分查询我发布的(published)或我参与的(joined)活动。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "我的活动查询入参")
public class MyActivityQueryVO extends BaseQueryVo {

    @Schema(description = "角色筛选：published=我发布的, joined=我参与的")
    private String role;

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
