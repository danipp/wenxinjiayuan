package com.demo.weixin.entity.activity;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * 活动实体
 * 活动状态根据 startTime/endTime 自动计算，status 字段为持久化的快照值，可由定时任务批量刷新。
 */
@Data
@NoArgsConstructor
@Document(collection = "activity")
@Schema(description = "活动")
public class Activity extends Base {

    /** 活动业务主键 */
    @Field
    private Long activityId;

    /** 发布者用户ID */
    @Field
    @Schema(description = "发布者用户ID")
    private Long publisherUserId;

    /** 活动标题 */
    @Field
    @Schema(description = "活动标题")
    private String title;

    /** 活动内容/描述 */
    @Field
    @Schema(description = "活动内容/描述")
    private String content;

    /** 活动地点 */
    @Field
    @Schema(description = "活动地点")
    private String location;

    /** 活动开始时间 */
    @Field
    @Schema(description = "活动开始时间")
    private Date startTime;

    /** 活动结束时间 */
    @Field
    @Schema(description = "活动结束时间")
    private Date endTime;

    /** 所属社区 */
    @Field
    @Schema(description = "所属社区")
    private String community;

    // [新增 2026-08-03 17:10] 社区数据隔离字段，community 已作为冗余文本字段保留
    /** 所属社区ID（用于数据隔离） */
    @Field
    @Schema(description = "所属社区ID")
    private Long communityId;

    /** 人数限制（0表示不限） */
    @Field
    @Schema(description = "人数限制（0表示不限）")
    private Integer maxLimit;

    /** 是否收集手机号 */
    @Field
    @Schema(description = "是否收集手机号")
    private Boolean collectPhone;

    /** 活动类型：1线上活动 2线下活动 3招募活动 */
    @Field
    @Schema(description = "活动类型：1线上活动 2线下活动 3招募活动")
    private Integer type;

    /** 活动状态：1未开始 2进行中 3已结束（持久化快照，实际展示以实时计算为准） */
    @Field
    @Schema(description = "活动状态：1未开始 2进行中 3已结束")
    private Integer status;

    /** 封面图URL */
    @Field
    @Schema(description = "封面图URL")
    private String coverImage;

    /** 活动标签 */
    @Field
    @Schema(description = "活动标签")
    private String tag;

    /** 参与人数 */
    @Field
    @Schema(description = "参与人数")
    private Integer participantCount;

    // ==================== 以下为非持久化字段（仅用于接口返回） ====================

    /** 当前用户是否已报名（非持久化，按请求用户实时计算） */
    @Transient
    @Schema(description = "当前用户是否已报名")
    private Boolean signedUp;

    /** 发布者昵称（非持久化，查询时冗余填充） */
    @Transient
    @Schema(description = "发布者昵称")
    private String authorName;

    /** 发布者头像（非持久化，查询时冗余填充） */
    @Transient
    @Schema(description = "发布者头像")
    private String authorAvatar;

    /** 状态文本（非持久化，按状态码实时计算） */
    @Transient
    @Schema(description = "状态文本")
    private String statusText;

    @Override
    public Long getID() {
        return activityId;
    }

    @Override
    public void setID(Long id) {
        this.activityId = id;
    }
}
