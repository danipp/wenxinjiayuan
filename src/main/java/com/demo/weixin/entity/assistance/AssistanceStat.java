package com.demo.weixin.entity.assistance;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 统计配置实体
 * 记录消费帮扶模块首页展示的统计数据项，支持管理员自定义或实时聚合。
 */
@Data
@NoArgsConstructor
@Document(collection = "assistanceStat")
@Schema(description = "统计配置")
public class AssistanceStat extends Base {

    /** 统计配置业务主键 */
    @Field
    private Long statId;

    /** 统计项标识：merchantCount shopCount enterpriseCount goodsCount */
    @Indexed
    @Field
    @Schema(description = "统计项标识")
    private String statKey;

    /** 统计项标签（如"爱心联盟商家"） */
    @Field
    @Schema(description = "统计项标签")
    private String statLabel;

    /** 统计值 */
    @Field
    @Schema(description = "统计值")
    private Long statValue;

    /** 是否自定义值（true=管理员配置 false=实时聚合） */
    @Field
    @Schema(description = "是否自定义值")
    private Boolean isCustom;

    /** 展示顺序 */
    @Field
    @Schema(description = "展示顺序")
    private Integer displayOrder;

    // [新增 2026-08-03 17:10] 社区数据隔离字段
    /** 所属社区ID（用于数据隔离） */
    @Field
    @Schema(description = "所属社区ID")
    private Long communityId;

    /** 所属社区名称（冗余字段） */
    @Field
    @Schema(description = "所属社区名称")
    private String communityName;

    @Override
    public Long getID() {
        return statId;
    }

    @Override
    public void setID(Long id) {
        this.statId = id;
    }
}
