package com.demo.weixin.entity.ad;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * 广告位实体
 * 用于管理各页面Banner广告、弹窗广告等。
 * 通过 position 字段区分广告投放位置，C端按 position + 有效时间 + 上架状态 查询。
 */
@Data
@NoArgsConstructor
@Document(collection = "ad")
@Schema(description = "广告位")
public class Ad extends Base {

    /** 广告业务主键 */
    @Field
    private Long adId;

    /** 广告位标识（如 home_banner / store_banner / square_banner） */
    @Field
    @Schema(description = "广告位标识")
    private String position;

    /** 广告标题 */
    @Field
    @Schema(description = "广告标题")
    private String title;

    /** 广告图片URL */
    @Field
    @Schema(description = "广告图片URL")
    private String imageUrl;

    /** 跳转类型：1活动 2商品 3店铺 4外部链接 0不跳转 */
    @Field
    @Schema(description = "跳转类型：1活动 2商品 3店铺 4外部链接 0不跳转")
    private Integer linkType;

    /** 跳转目标值（活动ID/商品ID/店铺ID/外部URL） */
    @Field
    @Schema(description = "跳转目标值")
    private String linkValue;

    /** 排序号（越小越靠前） */
    @Field
    @Schema(description = "排序号")
    private Integer sortNum;

    /** 广告状态：1上架 2下架 */
    @Field
    @Schema(description = "广告状态：1上架 2下架")
    private Integer status;

    /** 投放开始时间（null表示立即生效） */
    @Field
    @Schema(description = "投放开始时间")
    private Date startTime;

    /** 投放结束时间（null表示长期有效） */
    @Field
    @Schema(description = "投放结束时间")
    private Date endTime;

    @Override
    public Long getID() {
        return adId;
    }

    @Override
    public void setID(Long id) {
        this.adId = id;
    }
}
