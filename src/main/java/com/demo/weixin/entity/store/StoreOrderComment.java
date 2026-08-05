package com.demo.weixin.entity.store;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * 商城订单评价实体
 * 买家在订单完成后对商品进行评价，一个订单只能评价一次。
 */
@Data
@NoArgsConstructor
@Document(collection = "storeOrderComment")
@Schema(description = "订单评价")
public class StoreOrderComment extends Base {

    /** 评价业务主键 */
    @Field
    private Long commentId;

    /** 订单ID */
    @Field
    @Schema(description = "订单ID")
    private Long orderId;

    /** 商品ID */
    @Field
    @Schema(description = "商品ID")
    private Long goodsId;

    /** 评价用户ID */
    @Field
    @Schema(description = "评价用户ID")
    private Long userId;

    /** 用户昵称（冗余） */
    @Field
    @Schema(description = "用户昵称")
    private String userName;

    /** 用户头像（冗余） */
    @Field
    @Schema(description = "用户头像")
    private String userAvatar;

    /** 评分（1-5星） */
    @Field
    @Schema(description = "评分1-5")
    private Integer rating;

    /** 评价内容 */
    @Field
    @Schema(description = "评价内容")
    private String content;

    /** 评价图片URL列表 */
    @Field
    @Schema(description = "评价图片URL列表")
    private List<String> images;

    @Override
    public Long getID() {
        return commentId;
    }

    @Override
    public void setID(Long id) {
        this.commentId = id;
    }
}
