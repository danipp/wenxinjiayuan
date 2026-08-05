package com.demo.weixin.vo.store;

import com.demo.weixin.vo.BaseQueryVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 积分流水查询入参
 * [新增 2026-08-03 19:10] 用于积分明细分页查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "积分流水查询入参")
public class PointsRecordQueryVO extends BaseQueryVo {

    @Schema(description = "变动类型：1=获得，2=消耗，3=退还，不传则查全部")
    private Integer type;
}
