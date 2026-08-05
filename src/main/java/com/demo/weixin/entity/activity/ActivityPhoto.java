package com.demo.weixin.entity.activity;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * 活动照片实体
 * 用户上传的活动相关照片，支持点赞功能，通过 likedUserIds 列表记录点赞用户。
 */
@Data
@NoArgsConstructor
@Document(collection = "activityPhoto")
@Schema(description = "活动照片")
public class ActivityPhoto extends Base {

    /** 照片业务主键 */
    @Field
    private Long photoId;

    /** 活动ID */
    @Field
    @Schema(description = "活动ID")
    private Long activityId;

    /** 上传用户ID */
    @Field
    @Schema(description = "上传用户ID")
    private Long userId;

    /** 上传用户昵称（冗余） */
    @Field
    @Schema(description = "上传用户昵称")
    private String nickName;

    /** 图片URL */
    @Field
    @Schema(description = "图片URL")
    private String imageUrl;

    /** 点赞数 */
    @Field
    @Schema(description = "点赞数")
    private Integer likes;

    /** 点赞用户ID列表 */
    @Field
    @Schema(description = "点赞用户ID列表")
    private List<Long> likedUserIds;

    // ==================== 以下为非持久化字段（仅用于接口返回） ====================

    /** 当前用户是否已点赞（非持久化，按请求用户实时计算） */
    @Transient
    @Schema(description = "当前用户是否已点赞")
    private Boolean isLiked;

    @Override
    public Long getID() {
        return photoId;
    }

    @Override
    public void setID(Long id) {
        this.photoId = id;
    }
}
