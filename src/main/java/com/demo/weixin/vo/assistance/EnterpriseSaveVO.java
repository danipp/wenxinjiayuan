package com.demo.weixin.vo.assistance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 爱心企业保存入参（新增/编辑共用）
 */
@Data
@Schema(description = "爱心企业保存入参")
public class EnterpriseSaveVO {

    @Schema(description = "企业ID（编辑时传入）")
    private Long enterpriseId;

    @Schema(description = "企业名称")
    @NotBlank(message = "企业名称不能为空")
    private String name;

    @Schema(description = "企业Logo URL")
    private String logo;

    @Schema(description = "企业简介")
    private String description;

    @Schema(description = "联系人")
    private String contactName;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "企业地址")
    private String address;

    // [新增 2026-08-03 17:40] 社区数据隔离字段
    @Schema(description = "所属社区ID（数据隔离用）")
    private Long communityId;

    @Schema(description = "排序权重")
    private Integer sort;
}
