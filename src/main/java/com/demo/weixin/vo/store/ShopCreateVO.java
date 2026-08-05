package com.demo.weixin.vo.store;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 店铺创建/编辑入参
 */
@Data
@Schema(description = "店铺创建编辑入参")
public class ShopCreateVO {

    @Schema(description = "店铺ID（编辑时传入，新增时不传）")
    private Long shopId;

    @Schema(description = "店铺名称")
    @NotBlank(message = "店铺名称不能为空")
    private String name;

    @Schema(description = "店铺Logo URL")
    private String logo;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "店铺地址")
    private String address;

    @Schema(description = "店铺简介")
    private String description;

    // [新增 2026-08-03 17:40] 社区数据隔离字段
    @Schema(description = "所属社区ID（数据隔离用）")
    private Long communityId;
}
