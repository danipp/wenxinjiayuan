package com.demo.weixin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author zane
 */
@Schema(name = "获取oss签名入参")
@Data
public class OssPolicyVo {

//    @Schema(description = "用途", name = "purpose", required = true)
//    @NotBlank(message = "用途不能为空")
//    private String purpose;

    @Schema(description = "业务类型 ", name = "biz")
    private String biz;

    @Schema(description = "userId", name = "userId")
    private String userId;

    @Schema(description = "类型", name = "type")
    private String type;

}
