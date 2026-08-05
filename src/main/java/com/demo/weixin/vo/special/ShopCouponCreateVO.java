package com.demo.weixin.vo.special;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券创建/编辑入参
 */
@Schema(name = "优惠券创建入参")
@Data
public class ShopCouponCreateVO {

    @Schema(description = "优惠券ID（编辑时必传）")
    private Long couponId;

    @Schema(description = "店铺ID")
    @NotNull(message = "店铺ID不能为空")
    private Long shopId;

    @Schema(description = "优惠券标题")
    @NotBlank(message = "标题不能为空")
    private String title;

    @Schema(description = "抵扣金额")
    @NotNull(message = "抵扣金额不能为空")
    @DecimalMin(value = "0.01", message = "抵扣金额必须大于0")
    private BigDecimal money;

    @Schema(description = "最低消费门槛（0表示无门槛）")
    private BigDecimal minSpend = BigDecimal.ZERO;

    @Schema(description = "发行总量（0表示不限量）")
    private Integer total = 0;

    @Schema(description = "有效期开始时间")
    private Date startTime;

    @Schema(description = "有效期结束时间")
    private Date endTime;
}
