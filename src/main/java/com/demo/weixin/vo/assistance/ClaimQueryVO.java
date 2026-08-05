package com.demo.weixin.vo.assistance;

import com.demo.weixin.vo.BaseQueryVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 物资申领查询入参
 */
@Data
@Schema(description = "物资申领查询入参")
public class ClaimQueryVO extends BaseQueryVo {

    @Schema(description = "按用户筛选")
    private Long userId;

    @Schema(description = "按商品筛选")
    private Long goodsId;

    @Schema(description = "按状态筛选")
    private String status;

    @Schema(description = "视角：my我的 all全部")
    private String role;

    // [新增 2026-08-03 17:20] 社区数据隔离字段
    @Schema(description = "所属社区ID（数据隔离用，前端传入当前选中社区ID）")
    private Long communityId;
}
