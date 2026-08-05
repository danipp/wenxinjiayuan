package com.demo.weixin.vo.special;

import com.demo.weixin.vo.BaseQueryVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 店铺评价查询VO
 */
@Schema(name = "店铺评价查询")
@Data
public class ShopReviewQueryVO extends BaseQueryVo {

    @Schema(description = "店铺ID")
    private Long shopId;

    @Schema(description = "社区ID（数据隔离）")
    private Long communityId;


}
