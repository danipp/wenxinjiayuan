package com.demo.weixin.vo.special;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * 店铺评价创建入参
 */
@Schema(name = "店铺评价创建入参")
@Data
public class ShopReviewCreateVO {

    @Schema(description = "店铺ID")
    @NotNull(message = "店铺ID不能为空")
    private Long shopId;

    @Schema(description = "评分（1-5）")
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低1分")
    @Max(value = 5, message = "评分最高5分")
    private Integer rating;

    @Schema(description = "评价内容")
    @NotBlank(message = "评价内容不能为空")
    private String content;

    @Schema(description = "评价图片URL列表")
    private List<String> images;
}
