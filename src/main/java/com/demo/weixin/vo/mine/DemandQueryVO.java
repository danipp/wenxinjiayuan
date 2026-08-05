package com.demo.weixin.vo.mine;

import com.demo.weixin.vo.BaseQueryVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 需求/帮忙记录分页查询入参
 * 需求发布记录和帮忙记录共用 DemandRecord 表，通过 role 区分查询视角：
 * - role=1（发布者视角）：查询 publisherUserId = 当前用户 的记录
 * - role=2（帮忙者视角）：查询 helperUserId = 当前用户 的记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "需求/帮忙记录分页查询入参")
public class DemandQueryVO extends BaseQueryVo {

    @Schema(description = "视角：1发布者视角 2帮忙者视角")
    private Integer role;

    @Schema(description = "状态筛选：all全部/pending待帮忙/helping已接单/toEvaluate待评价/completed已完成/expired已过期")
    private String status;

    @Schema(description = "需求类型（如：代购、陪护、维修等）")
    private String requirement;

    @Schema(description = "排序方式：asc升序/desc降序（按服务时间排序）")
    private String sort;

    // [新增 2026-08-03 17:20] 社区数据隔离字段
    @Schema(description = "所属社区ID（数据隔离用，前端传入当前选中社区ID）")
    private Long communityId;
}
