package com.demo.weixin.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

/**
 * 事件
 */
@Data
@NoArgsConstructor
@Document(collection = "event")
public class Event extends Base {


    @Field
    private Long eventId; //对于业务实体的实际id
    private Long userId;
    private String sessionId;
    @Field
    @Schema(description = "链接")
    private String url;

    @Field
    @Schema(description = "参数")
    private String params;

    @Field
    @Schema(description = "事件名")
    private String eventName;

    /**
     * 1微信  2支付宝
     */
    @Field
    private Integer type = 1;


    @Override
    public Long getID() {
        return eventId;
    }

    @Override
    public void setID(Long id) {
        this.eventId = id;
    }

}
