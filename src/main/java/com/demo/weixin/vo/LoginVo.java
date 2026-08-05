package com.demo.weixin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zane
 */
@Schema(name = "登录信息报文")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginVo {

    @Schema(description = "登录成功token")
    private String token;

    @Schema(description = "openId")
    private String openId;

    /** [新增 2026-08-03 21:00] 用户角色：1=居民 2=志愿者 */
    @Schema(description = "用户角色：1=居民 2=志愿者")
    private Integer role;

    /** [新增 2026-08-03 21:00] 志愿者ID（居民为null） */
    @Schema(description = "志愿者ID")
    private String volunteerId;

}
