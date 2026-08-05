package com.demo.weixin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author zane
 */
@Schema(name = "微信用户信息补充入参")
@Data
public class WeChatComplementVo {


    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "加密的手机号授权code（居民身份认证）")
    private String decodeTelCode;

    @Schema(description = "头像")
    private String avatar;

    /** [新增 2026-08-03 21:30] 志愿者ID（志愿者身份认证，与手机号授权二选一） */
    @Schema(description = "志愿者ID（志愿者身份认证，与decodeTelCode二选一）")
    private String volunteerId;

}
