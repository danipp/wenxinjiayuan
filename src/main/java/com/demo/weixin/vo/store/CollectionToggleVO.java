package com.demo.weixin.vo.store;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 收藏切换入参
 * 买家收藏/取消收藏商品或店铺，已收藏则取消，未收藏则新增。
 */
@Data
@Schema(description = "收藏切换入参")
public class CollectionToggleVO {

    @Schema(description = "收藏目标ID（商品ID或店铺ID）")
    @NotNull(message = "收藏目标ID不能为空")
    private Long targetId;

    @Schema(description = "收藏类型：1商品收藏 2店铺收藏")
    @NotNull(message = "收藏类型不能为空")
    private Integer targetType;
}
