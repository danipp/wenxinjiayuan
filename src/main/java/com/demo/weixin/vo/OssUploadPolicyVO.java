package com.demo.weixin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "OSS直传上传凭证")
public class OssUploadPolicyVO {

    @Schema(description = "AccessKey ID")
    private String accessKeyId;

    @Schema(description = "上传策略（Base64编码）")
    private String policy;

    @Schema(description = "上传签名")
    private String signature;

    @Schema(description = "上传目录")
    private String dir;

    @Schema(description = "OSS上传地址")
    private String host;

    @Schema(description = "过期时间（Unix时间戳，单位：秒）")
    private String expire;
}