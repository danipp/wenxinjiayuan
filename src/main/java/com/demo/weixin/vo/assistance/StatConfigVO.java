package com.demo.weixin.vo.assistance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 统计配置入参（新增/编辑共用）
 */
@Data
@Schema(description = "统计配置入参")
public class StatConfigVO {

    @Schema(description = "统计项ID（编辑时传入）")
    private Long statId;

    @Schema(description = "统计项标识")
    @NotBlank(message = "统计项标识不能为空")
    private String statKey;

    @Schema(description = "统计项标签")
    @NotBlank(message = "统计项标签不能为空")
    private String statLabel;

    @Schema(description = "统计值")
    private Long statValue;

    @Schema(description = "是否自定义值")
    private Boolean isCustom;

    @Schema(description = "展示顺序")
    private Integer displayOrder;
}
