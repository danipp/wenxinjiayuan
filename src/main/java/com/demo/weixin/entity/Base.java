package com.demo.weixin.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
public abstract class Base {

    @Field
    protected Boolean del_flag = false;


    /**
     * 创建时间
     */
    @Field
    @Schema(description = "创建时间")
    protected Date createTime;

    /**
     * 最后更新时间
     */
    @Field
    @Schema(description = "最后更新时间")
    protected Date updateTime;

    public abstract Long getID();

    public abstract void setID(Long id);
}
