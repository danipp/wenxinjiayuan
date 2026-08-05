package com.demo.weixin.entity.special;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 社区特惠分类实体
 * 支持两级分类树：parentId=0 为一级分类，parentId=一级分类ID 为二级分类
 */
@Data
@NoArgsConstructor
@Document(collection = "specialCategory")
@Schema(description = "社区特惠分类")
public class SpecialCategory extends Base {

    /** 分类业务主键 */
    @Field
    private Long categoryId;

    /** 父分类ID（0表示一级分类） */
    @Field
    @Schema(description = "父分类ID（0表示一级分类）")
    private Long parentId;

    /** 分类名称 */
    @Field
    @Schema(description = "分类名称")
    private String name;

    /** 分类图标URL（一级分类使用） */
    @Field
    @Schema(description = "分类图标URL")
    private String icon;

    /** 排序值（越小越靠前） */
    @Field
    @Schema(description = "排序值")
    private Integer sort;

    /** 状态：1启用 2停用 */
    @Field
    @Schema(description = "状态：1启用 2停用")
    private Integer status;

    /** 所属社区ID（0表示全社区通用） */
    @Field
    @Schema(description = "所属社区ID（0表示全社区通用）")
    private Long communityId;

    @Override
    public Long getID() {
        return categoryId;
    }

    @Override
    public void setID(Long id) {
        this.categoryId = id;
    }
}
