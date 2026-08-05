package com.demo.weixin.entity.assistance;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 捐赠申请实体
 * 记录个人或企业提交的捐赠申请（资金/物资）及审核状态。
 */
@Data
@NoArgsConstructor
@Document(collection = "donationApply")
@Schema(description = "捐赠申请")
public class DonationApply extends Base {

    /** 捐赠申请业务主键 */
    @Field
    private Long donationId;

    /** 申请用户ID */
    @Indexed
    @Field
    @Schema(description = "申请用户ID")
    private Long userId;

    /** 申请者类型：individual个人 enterprise企业 */
    @Field
    @Schema(description = "申请者类型")
    private String userType;

    /** 关联企业ID（企业捐赠时填写） */
    @Field
    @Schema(description = "关联企业ID")
    private Long enterpriseId;

    /** 捐赠类型：money资金 goods物资 */
    @Field
    @Schema(description = "捐赠类型")
    private String donationType;

    /** 捐赠金额（资金捐赠时填写） */
    @Field
    @Schema(description = "捐赠金额")
    private BigDecimal amount;

    /** 物资名称（物资捐赠时填写） */
    @Field
    @Schema(description = "物资名称")
    private String goodsName;

    /** 物资数量 */
    @Field
    @Schema(description = "物资数量")
    private Integer goodsQuantity;

    /** 物资估值 */
    @Field
    @Schema(description = "物资估值")
    private BigDecimal goodsValue;

    /** 联系人姓名 */
    @Field
    @Schema(description = "联系人姓名")
    private String contactName;

    /** 联系电话 */
    @Field
    @Schema(description = "联系电话")
    private String contactPhone;

    /** 备注说明 */
    @Field
    @Schema(description = "备注说明")
    private String remark;

    /** 审核状态 */
    @Field
    @Schema(description = "审核状态")
    private String status;

    /** 审核备注 */
    @Field
    @Schema(description = "审核备注")
    private String auditRemark;

    /** 审核时间 */
    @Field
    @Schema(description = "审核时间")
    private Date auditTime;

    // [新增 2026-08-03 17:10] 社区数据隔离字段
    /** 所属社区ID（用于数据隔离） */
    @Field
    @Schema(description = "所属社区ID")
    private Long communityId;

    /** 所属社区名称（冗余字段） */
    @Field
    @Schema(description = "所属社区名称")
    private String communityName;

    @Override
    public Long getID() {
        return donationId;
    }

    @Override
    public void setID(Long id) {
        this.donationId = id;
    }
}
