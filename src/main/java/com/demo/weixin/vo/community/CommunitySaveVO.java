package com.demo.weixin.vo.community;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 社区保存入参（新增/编辑共用）
 */
@Data
@Schema(description = "社区保存入参")
public class CommunitySaveVO {

    @Schema(description = "社区ID（编辑时必传，新增时不传）")
    private Long communityId;

    @Schema(description = "社区名称")
    @NotBlank(message = "社区名称不能为空")
    private String name;

    @Schema(description = "社区地址")
    private String address;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "联系人")
    private String contactName;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "社区简介")
    private String description;

    @Schema(description = "社区Logo URL")
    private String logo;

    @Schema(description = "状态：1启用 2禁用")
    private Integer status;

    @Schema(description = "排序权重（越小越靠前）")
    private Integer sort;
}
