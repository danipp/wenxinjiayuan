package com.demo.weixin.entity.special;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * 优惠券领券记录实体
 * 记录用户领取的优惠券，用于核销和防重复领取
 */
@Data
@NoArgsConstructor
@Document(collection = "shopCouponRecord")
@Schema(description = "优惠券领券记录")
public class ShopCouponRecord extends Base {

    /** 记录业务主键 */
    @Field
    private Long recordId;

    /** 优惠券ID */
    @Field
    @Schema(description = "优惠券ID")
    private Long couponId;

    /** 店铺ID */
    @Field
    @Schema(description = "店铺ID")
    private Long shopId;

    /** 领取用户ID */
    @Field
    @Schema(description = "领取用户ID")
    private Long userId;

    /** 状态：1未使用 2已使用 3已过期 */
    @Field
    @Schema(description = "状态：1未使用 2已使用 3已过期")
    private Integer status;

    /** 领取时间 */
    @Field
    @Schema(description = "领取时间")
    private Date claimTime;

    /** 使用时间 */
    @Field
    @Schema(description = "使用时间")
    private Date useTime;

    @Override
    public Long getID() {
        return recordId;
    }

    @Override
    public void setID(Long id) {
        this.recordId = id;
    }
}
