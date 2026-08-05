package com.demo.weixin.vo.ad;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 广告位保存入参（新增/编辑共用）
 */
@Data
@Schema(description = "广告位保存入参")
public class AdSaveVO {

    @Schema(description = "广告ID（编辑时传，新增不传）")
    private Long adId;

    @Schema(description = "广告位标识")
    private String position;

    @Schema(description = "广告标题")
    private String title;

    @Schema(description = "广告图片URL")
    private String imageUrl;

    @Schema(description = "跳转类型：1活动 2商品 3店铺 4外部链接 0不跳转")
    private Integer linkType;

    @Schema(description = "跳转目标值")
    private String linkValue;

    @Schema(description = "排序号")
    private Integer sortNum;

    @Schema(description = "投放开始时间")
    private Date startTime;

    @Schema(description = "投放结束时间")
    private Date endTime;
}
