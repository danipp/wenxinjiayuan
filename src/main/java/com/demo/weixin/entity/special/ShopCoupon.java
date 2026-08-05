package com.demo.weixin.entity.special;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 店铺优惠券实体
 * 由商家/管理员创建，用户可领取后在消费时抵扣
 */
@Data
@NoArgsConstructor
@Document(collection = "shopCoupon")
@Schema(description = "店铺优惠券")
public class ShopCoupon extends Base {

    /** 优惠券业务主键 */
    @Field
    private Long couponId;

    /** 关联店铺ID */
    @Field
    @Schema(description = "店铺ID")
    private Long shopId;

    /** 优惠券标题（如：满100元可用） */
    @Field
    @Schema(description = "优惠券标题")
    private String title;

    /** 抵扣金额 */
    @Field
    @Schema(description = "抵扣金额")
    private BigDecimal money;

    /** 最低消费门槛（0表示无门槛） */
    @Field
    @Schema(description = "最低消费门槛")
    private BigDecimal minSpend;

    /** 发行总量（0表示不限量） */
    @Field
    @Schema(description = "发行总量（0表示不限量）")
    private Integer total;

    /** 已领取数量 */
    @Field
    @Schema(description = "已领取数量")
    private Integer claimedCount;

    /** 状态：1进行中 2已过期 3已下架 */
    @Field
    @Schema(description = "状态：1进行中 2已过期 3已下架")
    private Integer status;

    /** 有效期开始时间 */
    @Field
    @Schema(description = "有效期开始时间")
    private Date startTime;

    /** 有效期结束时间 */
    @Field
    @Schema(description = "有效期结束时间")
    private Date endTime;

    /** 所属社区ID（数据隔离） */
    @Field
    @Schema(description = "所属社区ID")
    private Long communityId;

    /** 当前用户是否已领取（非持久化，查询时动态填充） */
    @Transient
    @Schema(description = "当前用户是否已领取")
    private Boolean claimed;

    @Override
    public Long getID() {
        return couponId;
    }

    @Override
    public void setID(Long id) {
        this.couponId = id;
    }
}
