package com.demo.weixin.entity.store;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 商城收藏实体
 * 复用同一张表存储商品收藏和店铺收藏，通过 targetType 区分。
 */
@Data
@NoArgsConstructor
@Document(collection = "storeCollection")
@Schema(description = "商城收藏")
public class StoreCollection extends Base {

    /** 收藏业务主键 */
    @Field
    private Long collectionId;

    /** 用户ID */
    @Field
    @Schema(description = "用户ID")
    private Long userId;

    /** 收藏目标ID（商品ID或店铺ID） */
    @Field
    @Schema(description = "收藏目标ID")
    private Long targetId;

    /** 收藏类型：1商品收藏 2店铺收藏 */
    @Field
    @Schema(description = "收藏类型：1商品 2店铺")
    private Integer targetType;

    /** 被收藏的商品信息（targetType=1时有值，非持久化） */
    @Transient
    @Schema(description = "被收藏的商品信息")
    private StoreGoods goods;

    /** 被收藏的店铺信息（targetType=2时有值，非持久化） */
    @Transient
    @Schema(description = "被收藏的店铺信息")
    private StoreShop shop;

    @Override
    public Long getID() {
        return collectionId;
    }

    @Override
    public void setID(Long id) {
        this.collectionId = id;
    }
}
