package com.demo.weixin.entity.mine;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * 居民认证实体
 * 记录用户提交的社区居民认证信息及审核状态。
 * 每个用户仅维护一条认证记录，重复提交覆盖原有待审核记录。
 */
@Data
@NoArgsConstructor
@Document(collection = "residentCertification")
@Schema(description = "居民认证")
public class ResidentCertification extends Base {

    /** 认证记录业务主键 */
    @Field
    private Long certificationId;

    /** 用户ID（所属用户） */
    @Indexed
    @Field
    @Schema(description = "用户ID")
    private Long userId;

    /** 手机号（微信授权获取） */
    @Field
    @Schema(description = "手机号")
    private String phone;

    /** 社区名称 */
    @Field
    @Schema(description = "社区名称")
    private String communityName;

    /** 真实姓名 */
    @Field
    @Schema(description = "真实姓名")
    private String realName;

    /** 身份证号（可选） */
    @Field
    @Schema(description = "身份证号")
    private String idCard;

    /** 居住地址 */
    @Field
    @Schema(description = "居住地址")
    private String address;

    /** 认证状态：PENDING(待审核) APPROVED(已通过) REJECTED(已拒绝) */
    @Field
    @Schema(description = "认证状态")
    private String status;

    /** 审核备注（拒绝原因等） */
    @Field
    @Schema(description = "审核备注")
    private String auditRemark;

    /** 审核时间 */
    @Field
    @Schema(description = "审核时间")
    private Date auditTime;

    @Override
    public Long getID() {
        return certificationId;
    }

    @Override
    public void setID(Long id) {
        this.certificationId = id;
    }
}
