package com.demo.weixin.entity.special;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * 店铺评价实体
 * 用户对社区特惠店铺的评价，包含评分和文字内容
 */
@Data
@NoArgsConstructor
@Document(collection = "shopReview")
@Schema(description = "店铺评价")
public class ShopReview extends Base {

    /** 评价业务主键 */
    @Field
    private Long reviewId;

    /** 店铺ID */
    @Field
    @Schema(description = "店铺ID")
    private Long shopId;

    /** 评价用户ID */
    @Field
    @Schema(description = "评价用户ID")
    private Long userId;

    /** 评价用户昵称（冗余） */
    @Field
    @Schema(description = "评价用户昵称")
    private String userName;

    /** 评价用户头像（冗余） */
    @Field
    @Schema(description = "评价用户头像")
    private String userAvatar;

    /** 评分（1-5） */
    @Field
    @Schema(description = "评分（1-5）")
    private Integer rating;

    /** 评价内容 */
    @Field
    @Schema(description = "评价内容")
    private String content;

    /** 评价图片URL列表 */
    @Field
    @Schema(description = "评价图片URL列表")
    private List<String> images;

    /** 所属社区ID（数据隔离） */
    @Field
    @Schema(description = "所属社区ID")
    private Long communityId;

    @Override
    public Long getID() {
        return reviewId;
    }

    @Override
    public void setID(Long id) {
        this.reviewId = id;
    }
}
