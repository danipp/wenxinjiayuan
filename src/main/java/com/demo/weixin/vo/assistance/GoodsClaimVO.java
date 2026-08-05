package com.demo.weixin.vo.assistance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 物资申领入参
 */
@Data
@Schema(description = "物资申领入参")
public class GoodsClaimVO {

    @Schema(description = "商品ID")
    @NotNull(message = "商品ID不能为空")
    private Long goodsId;

    @Schema(description = "申领数量")
    @NotNull(message = "申领数量不能为空")
    private Integer claimCount;

    @Schema(description = "申领原因")
    @NotBlank(message = "申领原因不能为空")
    private String claimReason;

    @Schema(description = "联系人")
    @NotBlank(message = "联系人不能为空")
    private String contactName;

    @Schema(description = "联系电话")
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    @Schema(description = "收货地址")
    @NotBlank(message = "收货地址不能为空")
    private String address;
}
