package com.demo.weixin.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


/**
 * H5落地页访问日志
 * @author rosun
 */
@Data
@NoArgsConstructor
@Document(collection = "LandingPageH5Log")
public class LandingPageH5Log extends Base {

    @Field
    private Long id;

    @Schema(description = "落地页名称")
    private String name;

    @Schema(description = "全URL")
    private String url;

    @Schema(description = "参数ID-1")
    private String paramId1;
    
    @Schema(description = "参数ID-2")
    private String paramId2;

    /**
     * IP地址
     */
    private String ip;

    @Schema(description = "用户唯一Id,基于客户端浏览器cookie")
    private String uuid;

    /**
     * user-agent
     */
    private String ua;
   

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public void setID(Long id) {
        this.id = id;
    }

}
