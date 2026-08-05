package com.demo.weixin.vo.assistance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 帮扶申请入参
 */
@Data
@Schema(description = "帮扶申请入参")
public class AssistanceApplyVO {

    @Schema(description = "申请人姓名")
    @NotBlank(message = "申请人姓名不能为空")
    private String applicantName;

    @Schema(description = "联系电话")
    @NotBlank(message = "联系电话不能为空")
    private String applicantPhone;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "居住地址")
    @NotBlank(message = "居住地址不能为空")
    private String address;

    @Schema(description = "家庭情况说明")
    @NotBlank(message = "家庭情况说明不能为空")
    private String familySituation;

    @Schema(description = "帮扶类型：living生活 medical医疗 education教育 employment就业")
    @NotBlank(message = "帮扶类型不能为空")
    private String assistanceType;

    @Schema(description = "困难描述")
    @NotBlank(message = "困难描述不能为空")
    private String difficultyDesc;

    @Schema(description = "期望帮扶内容")
    private String desiredHelp;

    // [新增 2026-08-03 17:40] 社区数据隔离字段
    @Schema(description = "所属社区ID（数据隔离用）")
    private Long communityId;

    @Schema(description = "备注")
    private String remark;
}
