package com.demo.weixin.vo.assistance;

import com.demo.weixin.vo.BaseQueryVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 捐赠申请查询入参
 */
@Data
@Schema(description = "捐赠申请查询入参")
public class DonationQueryVO extends BaseQueryVo {

    @Schema(description = "按用户筛选")
    private Long userId;

    @Schema(description = "按捐赠类型筛选：money资金 goods物资")
    private String donationType;

    @Schema(description = "按状态筛选")
    private String status;

    @Schema(description = "视角：my我的 apply申请列表")
    private String role;

    // [新增 2026-08-03 17:20] 社区数据隔离字段
    @Schema(description = "所属社区ID（数据隔离用，前端传入当前选中社区ID）")
    private Long communityId;
}
