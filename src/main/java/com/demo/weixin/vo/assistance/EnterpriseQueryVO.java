package com.demo.weixin.vo.assistance;

import com.demo.weixin.vo.BaseQueryVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 爱心企业查询入参
 */
@Data
@Schema(description = "爱心企业查询入参")
public class EnterpriseQueryVO extends BaseQueryVo {

    @Schema(description = "按状态筛选")
    private String status;

    @Schema(description = "按名称模糊搜索")
    private String name;

    // [新增 2026-08-03 17:20] 社区数据隔离字段
    @Schema(description = "所属社区ID（数据隔离用，前端传入当前选中社区ID）")
    private Long communityId;
}
