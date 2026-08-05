package com.demo.weixin.entity.community;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

/**
 * 社区实体
 * 由管理员后台维护，用户和各业务模块通过 communityId 关联实现数据隔离。
 * 冗余 communityName 文本字段在各业务实体中保留，避免查询时频繁关联。
 */
@Data
@NoArgsConstructor
@Document(collection = "community")
@Schema(description = "社区")
public class Community extends Base {

    /** 社区业务主键 */
    @Field
    private Long communityId;

    /** 社区名称 */
    @Field
    @Schema(description = "社区名称")
    private String name;

    /** 社区地址 */
    @Field
    @Schema(description = "社区地址")
    private String address;

    /** 经度 */
    @Field
    @Schema(description = "经度")
    private BigDecimal longitude;

    /** 纬度 */
    @Field
    @Schema(description = "纬度")
    private BigDecimal latitude;

    /** 联系人 */
    @Field
    @Schema(description = "联系人")
    private String contactName;

    /** 联系电话 */
    @Field
    @Schema(description = "联系电话")
    private String contactPhone;

    /** 社区简介 */
    @Field
    @Schema(description = "社区简介")
    private String description;

    /** 社区Logo URL */
    @Field
    @Schema(description = "社区Logo URL")
    private String logo;

    /** 状态：1启用 2禁用 */
    @Field
    @Schema(description = "状态：1启用 2禁用")
    private Integer status;

    /** 排序权重（越小越靠前） */
    @Field
    @Schema(description = "排序权重")
    private Integer sort;

    @Override
    public Long getID() {
        return communityId;
    }

    @Override
    public void setID(Long id) {
        this.communityId = id;
    }
}
