package com.demo.weixin.entity.activity;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * 活动报名实体
 * 记录用户报名参加某活动的信息，一个用户对同一活动只能报名一次。
 */
@Data
@NoArgsConstructor
@Document(collection = "activitySignup")
@Schema(description = "活动报名")
public class ActivitySignup extends Base {

    /** 报名记录业务主键 */
    @Field
    private Long signupId;

    /** 活动ID */
    @Field
    @Schema(description = "活动ID")
    private Long activityId;

    /** 报名用户ID */
    @Field
    @Schema(description = "报名用户ID")
    private Long userId;

    /** 用户昵称（冗余） */
    @Field
    @Schema(description = "用户昵称")
    private String nickName;

    /** 用户头像（冗余） */
    @Field
    @Schema(description = "用户头像")
    private String avatar;

    /** 联系手机号（活动开启收集手机号时填写） */
    @Field
    @Schema(description = "联系手机号")
    private String phone;

    /** 加入时间 */
    @Field
    @Schema(description = "加入时间")
    private Date joinTime;

    @Override
    public Long getID() {
        return signupId;
    }

    @Override
    public void setID(Long id) {
        this.signupId = id;
    }
}
