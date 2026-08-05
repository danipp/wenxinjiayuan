package com.demo.weixin.vo.assistance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统计查询入参
 */
@Data
@Schema(description = "统计查询入参")
public class StatQueryVO {

    @Schema(description = "查询模式：real实时聚合 custom管理员配置 all全部（默认all）")
    private String mode = "all";

    // [新增 2026-08-03 17:20] 社区数据隔离字段
    @Schema(description = "所属社区ID（数据隔离用，前端传入当前选中社区ID）")
    private Long communityId;
}
