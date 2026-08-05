package com.demo.weixin.entity.activity;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * 活动模板实体
 * 预设的活动模板，用户可基于模板快速创建同款活动。
 * usedCount 记录模板被使用的次数，用于热门排序。
 */
@Data
@NoArgsConstructor
@Document(collection = "activityTemplate")
@Schema(description = "活动模板")
public class ActivityTemplate extends Base {

    /** 模板业务主键 */
    @Field
    private Long templateId;

    /** 模板标题 */
    @Field
    @Schema(description = "模板标题")
    private String title;

    /** 活动内容/介绍 */
    @Field
    @Schema(description = "活动内容")
    private String content;

    /** 活动地点（模板预设值，用户可在此基础上修改） */
    @Field
    @Schema(description = "活动地点（模板预设值，用户可在此基础上修改）")
    private String location;

    /** 活动开始时间（模板预设值，用户可在此基础上修改） */
    @Field
    @Schema(description = "活动开始时间（模板预设值，用户可在此基础上修改）")
    private Date startTime;

    /** 活动结束时间（模板预设值，用户可在此基础上修改） */
    @Field
    @Schema(description = "活动结束时间（模板预设值，用户可在此基础上修改）")
    private Date endTime;

    /** 所属社区（模板预设值，用户可在此基础上修改） */
    @Field
    @Schema(description = "所属社区（模板预设值，用户可在此基础上修改）")
    private String community;

    /** 是否收集手机号（模板预设值，用户可在此基础上修改） */
    @Field
    @Schema(description = "是否收集手机号（模板预设值，用户可在此基础上修改）")
    private Boolean collectPhone;

    /** 封面图URL */
    @Field
    @Schema(description = "封面图URL")
    private String coverImage;

    /** 列表展示图URL */
    @Field
    @Schema(description = "列表展示图URL")
    private String image;

    /** 活动标签（如 时光讲堂、志愿服务等） */
    @Field
    @Schema(description = "活动标签")
    private String tag;

    /** 活动类型：1线上活动 2线下活动 3招募活动 */
    @Field
    @Schema(description = "活动类型")
    private Integer type;

    /** 人数限制 */
    @Field
    @Schema(description = "人数限制")
    private Integer maxLimit;

    /** 参与人数展示文本（如 "25728"） */
    @Field
    @Schema(description = "参与人数展示文本")
    private String participants;

    /** 使用次数（被用户用来创建活动的次数） */
    @Field
    @Schema(description = "使用次数")
    private Integer usedCount;

    /** 累计参与人数 */
    @Field
    @Schema(description = "累计参与人数")
    private Integer totalJoined;

    /** 模板分类 */
    @Field
    @Schema(description = "模板分类")
    private String category;

    /** 排序权重（越大越靠前） */
    @Field
    @Schema(description = "排序权重")
    private Integer sort;

    @Override
    public Long getID() {
        return templateId;
    }

    @Override
    public void setID(Long id) {
        this.templateId = id;
    }
}
