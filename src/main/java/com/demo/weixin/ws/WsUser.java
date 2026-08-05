package com.demo.weixin.ws;

import org.springframework.data.mongodb.core.mapping.Field;

import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WsUser {

	/**
     * @see  com.demo.weixin.enums.PartnerTypeEnum  
     */
    @Schema(description = "类型，OWNER | DEVELOPER | AGENT")
    @Field
    private String type;
    /**
     * 伙伴id，可能是ownerId，或者developerId 或者agentId
     */
    private Long partnerId;
    
    @Schema(description = "手机号")
    private String cellphone;
    
    /**
     * 创建时间;
     */
    @Builder.Default
    private long time  = System.currentTimeMillis();
    
    public String toString() {
    	return JSONUtil.toJsonStr(this);
    }
}
