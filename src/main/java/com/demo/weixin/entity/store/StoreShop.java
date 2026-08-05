package com.demo.weixin.entity.store;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

/**
 * 商城店铺实体
 * 每个卖家用户可创建一个店铺，商品可关联到店铺。
 */
@Data
@NoArgsConstructor
@Document(collection = "storeShop")
@Schema(description = "商城店铺")
public class StoreShop extends Base {

    /** 店铺业务主键 */
    @Field
    private Long shopId;

    /** 卖家用户ID（关联User.userId） */
    @Field
    @Schema(description = "卖家用户ID")
    private Long sellerUserId;

    /** 店铺名称 */
    @Field
    @Schema(description = "店铺名称")
    private String name;

    /** 店铺Logo URL */
    @Field
    @Schema(description = "店铺Logo")
    private String logo;

    /** 联系电话 */
    @Field
    @Schema(description = "联系电话")
    private String phone;

    /** 店铺地址 */
    @Field
    @Schema(description = "店铺地址")
    private String address;

    /** 店铺简介 */
    @Field
    @Schema(description = "店铺简介")
    private String description;

    /** 店铺状态：1营业中 2歇业中 */
    @Field
    @Schema(description = "店铺状态：1营业中 2歇业中")
    private Integer status;

    /** 店铺商品总数（冗余字段，定期同步） */
    @Field
    @Schema(description = "商品总数")
    private Integer goodsCount;

    // [新增 2026-07-31 17:21] 店铺统计冗余字段，关注/取消关注和订单核销时同步更新
    /** 关注数（卖家关注了多少人，即 FollowRecord 中 followerUserId=sellerUserId 的数量） */
    @Field
    @Schema(description = "关注数  卖家关注了多少人")
    private Integer followCount;

    /** 粉丝数（多少人关注了卖家，即 FollowRecord 中 targetUserId=sellerUserId 的数量；关注=收藏，故粉丝数=收藏数） */
    @Field
    @Schema(description = "粉丝数  多少人关注了卖家")
    private Integer fansCount;

    /** 月销量（订单核销完成时 +count，冗余字段） */
    @Field
    @Schema(description = "月销量")
    private Integer monthlySales;

    // [新增 2026-08-03 17:10] 社区数据隔离字段
    /** 所属社区ID（用于数据隔离） */
    @Field
    @Schema(description = "所属社区ID")
    private Long communityId;

    /** 所属社区名称（冗余字段） */
    @Field
    @Schema(description = "所属社区名称")
    private String communityName;

    // [新增 2026-08-04 10:00] 社区特惠模块扩展字段
    /** 一级分类ID（关联 SpecialCategory.categoryId，parentId=0 的分类） */
    @Field
    @Schema(description = "一级分类ID")
    private Long cat1Id;

    /** 二级分类ID（关联 SpecialCategory.categoryId，parentId=一级分类ID） */
    @Field
    @Schema(description = "二级分类ID")
    private Long cat2Id;

    /** 封面图URL（社区特惠列表展示用，与logo区分） */
    @Field
    @Schema(description = "封面图URL")
    private String coverImage;

    /** 纬度 */
    @Field
    @Schema(description = "纬度")
    private Double latitude;

    /** 经度 */
    @Field
    @Schema(description = "经度")
    private Double longitude;

    /** 店铺评分（冗余字段，如 4.9） */
    @Field
    @Schema(description = "店铺评分")
    private Double rating;

    /** 起步价（冗余字段，店铺最低商品价格） */
    @Field
    @Schema(description = "起步价")
    private BigDecimal startPrice;

    /** 是否新品（7天内创建，非持久化，查询时动态计算） */
    @Transient
    @Schema(description = "是否新品")
    private Boolean isNew;

    @Override
    public Long getID() {
        return shopId;
    }

    @Override
    public void setID(Long id) {
        this.shopId = id;
    }
}
