package com.demo.weixin.vo.store;

import com.demo.weixin.vo.BaseQueryVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 店铺分页查询入参
 * [新增 2026-08-03 18:55] 用于C端店铺列表分页查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "店铺分页查询入参")
public class ShopPageQueryVO extends BaseQueryVo {

    @Schema(description = "关键词（模糊搜索店铺名称）")
    private String keyword;

    // [新增 2026-08-03 17:20] 社区数据隔离字段
    @Schema(description = "所属社区ID（数据隔离用，前端传入当前选中社区ID）")
    private Long communityId;

    @Schema(description = "店铺状态：1营业中 2休息中，不传则查全部")
    private Integer status;
}
