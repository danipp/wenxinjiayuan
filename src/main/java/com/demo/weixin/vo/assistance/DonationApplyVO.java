package com.demo.weixin.vo.assistance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 捐赠申请入参
 */
@Data
@Schema(description = "捐赠申请入参")
public class DonationApplyVO {

    @Schema(description = "申请者类型：individual个人 enterprise企业")
    @NotBlank(message = "申请者类型不能为空")
    private String userType;

    @Schema(description = "关联企业ID（企业捐赠时填写）")
    private Long enterpriseId;

    @Schema(description = "捐赠类型：money资金 goods物资")
    @NotBlank(message = "捐赠类型不能为空")
    private String donationType;

    @Schema(description = "捐赠金额（资金捐赠时必填）")
    private BigDecimal amount;

    @Schema(description = "物资名称（物资捐赠时必填）")
    private String goodsName;

    @Schema(description = "物资数量")
    private Integer goodsQuantity;

    @Schema(description = "物资估值")
    private BigDecimal goodsValue;

    @Schema(description = "联系人姓名")
    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    @Schema(description = "联系电话")
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    // [新增 2026-08-03 17:40] 社区数据隔离字段
    @Schema(description = "所属社区ID（数据隔离用）")
    private Long communityId;

    @Schema(description = "备注说明")
    private String remark;
}
