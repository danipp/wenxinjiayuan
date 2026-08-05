package com.demo.weixin.vo.mine;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 打卡记录创建入参
 */
@Data
@Schema(description = "打卡记录创建入参")
public class CheckinCreateVO {

    @Schema(description = "相框编号")
    @NotBlank(message = "相框编号不能为空")
    private String frameNo;

    @Schema(description = "相框名称")
    private String frameName;

    @Schema(description = "相框图片URL")
    private String frameImage;

    @Schema(description = "打卡位置")
    private String location;
}
