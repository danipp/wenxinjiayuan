package com.demo.weixin.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

/**
 * 用户表
 * 使用手机号和密码登录；（openId等小程序或者公众号就绪再生效）
 */
@Data
@NoArgsConstructor
@Document(collection = "user")
public class User extends Base {


    @Field
    private Long userId; //对于业务实体的实际id

    /**
     * 昵称（登录后显示），默认 是用户+手机尾号4位
     */
    @Field
    private String nickName;


    /**
     * 登录用的密码
     */
    @Field
    private String passWord;

    /**
     * 手机
     */
    @Field
    private String cellphone;

    /**
     * 头像
     */
    @Field
    private String avatar;
    /**
     * 描述
     */
    @Field
    private String description;

    /**
     * 微信OpenId
     */
    @Field
    private String openId;


    /**
     * 微信unionId
     */
    @Field
    private String unionId;

    @Field
    @Schema(description = "是否开发者")
    private Boolean developer = false;

    @Field
    @Schema(description = "开发者Id")
    private Long developerId;

    @Field
    @Schema(description = "是否商户")
    private Boolean owner = false;

    @Field
    @Schema(description = "商户Id")
    private Long ownerId;

    @Field
    @Schema(description = "是否代理")
    private Boolean agent = false;

    @Field
    @Schema(description = "代理Id")
    private Long agentId;

    @Field
    @Schema(description = "1微信 2支付宝")
    private Integer type=1;

    @Field
    @Schema(description = "冗余参数")
    private String extParams;

    // [新增 2026-08-03 17:10] 社区数据隔离字段
    /** 所属社区ID（用于数据隔离，用户切换社区后各模块按此字段过滤） */
    @Field
    @Schema(description = "所属社区ID")
    private Long communityId;

    /** 所属社区名称（冗余字段，避免查询时关联社区表） */
    @Field
    @Schema(description = "所属社区名称")
    private String communityName;

    // [新增 2026-08-03 21:00] 用户角色与志愿者字段
    /** 用户角色：1=居民（默认），2=志愿者 */
    @Field
    @Schema(description = "用户角色：1=居民 2=志愿者")
    private Integer role = 1;

    /** 志愿者ID（管理员手动录入或第三方平台返回，居民为null） */
    @Field
    @Schema(description = "志愿者ID")
    private String volunteerId;

    /** 志愿者状态：0=未激活 1=正常 2=停用（居民为null） */
    @Field
    @Schema(description = "志愿者状态：0=未激活 1=正常 2=停用")
    private Integer volunteerStatus;

    @Override
    public Long getID() {
        return userId;
    }

    @Override
    public void setID(Long id) {
        this.userId = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        return Objects.equals(userId, other.userId);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(userId);
        return result;
    }
}
