package com.demo.weixin.vo.mine;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 达人排行榜入参
 */
@Data
@Schema(description = "达人排行榜入参")
public class LeaderboardVO {

    @Schema(description = "排行榜类型：1互助达人 2活动达人")
    private Integer type;

    @Schema(description = "社区名称（可选筛选）")
    private String community;

    @Schema(description = "限制条数，默认20")
    private Integer limit;
}
