package com.demo.weixin.entity.activity;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 活动评价实体
 * 用户在活动结束后对活动进行评价，包含评分、表情、状态标签和评价内容。
 */
@Data
@NoArgsConstructor
@Document(collection = "activityComment")
@Schema(description = "活动评价")
public class ActivityComment extends Base {

    /** 评价业务主键 */
    @Field
    private Long commentId;

    /** 活动ID */
    @Field
    @Schema(description = "活动ID")
    private Long activityId;

    /** 评价用户ID */
    @Field
    @Schema(description = "评价用户ID")
    private Long userId;

    /** 用户昵称（冗余） */
    @Field
    @Schema(description = "用户昵称")
    private String nickName;

    /** 用户头像（冗余） */
    @Field
    @Schema(description = "用户头像")
    private String avatar;

    /** 评分（1-5星） */
    @Field
    @Schema(description = "评分1-5")
    private Integer score;

    /** 评价表情（如emoji表情符号） */
    @Field
    @Schema(description = "评价表情")
    private String emoji;

    /** 状态标签文本（如"非常满意"、"值得推荐"） */
    @Field
    @Schema(description = "状态标签文本")
    private String statusText;

    /** 评价内容 */
    @Field
    @Schema(description = "评价内容")
    private String content;

    @Override
    public Long getID() {
        return commentId;
    }

    @Override
    public void setID(Long id) {
        this.commentId = id;
    }
}
