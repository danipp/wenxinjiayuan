package com.demo.weixin.vo.special;

import com.demo.weixin.vo.BaseQueryVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 社区特惠店铺查询VO
 * 支持分类筛选、排序、关键词搜索、分页
 */
@Schema(name = "社区特惠店铺查询")
@Data
public class SpecialShopQueryVO extends BaseQueryVo {

    @Schema(description = "一级分类ID（可选）")
    private Long cat1Id;

    @Schema(description = "二级分类ID（可选）")
    private Long cat2Id;

    @Schema(description = "关键词搜索（匹配店铺名称）")
    private String keyword;

    @Schema(description = "社区ID（数据隔离）")
    private Long communityId;

    @Schema(description = "排序方式：sales=销量降序，price_asc=价格升序，price_desc=价格降序，rating=评分降序")
    private String sort;

    @Schema(description = "是否只看高评分（>=4.8）")
    private Boolean highRating;

    @Schema(description = "是否只看新品（7天内创建）")
    private Boolean isNew;

}
