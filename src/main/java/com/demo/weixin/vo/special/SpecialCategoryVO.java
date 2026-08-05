package com.demo.weixin.vo.special;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 社区特惠分类树返回VO
 * 一级分类包含子分类列表
 */
@Schema(name = "社区特惠分类树")
@Data
public class SpecialCategoryVO {

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "父分类ID（0表示一级分类）")
    private Long parentId;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "分类图标URL")
    private String icon;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "子分类列表（仅一级分类有值）")
    private List<SpecialCategoryVO> children;
}
