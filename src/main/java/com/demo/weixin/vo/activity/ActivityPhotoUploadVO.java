package com.demo.weixin.vo.activity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 活动照片上传入参
 * 图片已通过OSS上传，本入参仅记录照片信息。
 */
@Data
@Schema(description = "活动照片上传入参")
public class ActivityPhotoUploadVO {

    @Schema(description = "活动ID")
    @NotNull(message = "活动ID不能为空")
    private Long activityId;

    @Schema(description = "照片URL（OSS地址）")
    @NotBlank(message = "照片URL不能为空")
    private String imageUrl;
}
