package com.demo.weixin.entity.assistance;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * 帮扶申请实体
 * 记录困难用户提交的帮扶申请及审核状态。
 */
@Data
@NoArgsConstructor
@Document(collection = "assistanceApply")
@Schema(description = "帮扶申请")
public class AssistanceApply extends Base {

    /** 帮扶申请业务主键 */
    @Field
    private Long applyId;

    /** 申请用户ID */
    @Indexed
    @Field
    @Schema(description = "申请用户ID")
    private Long userId;

    /** 申请人姓名 */
    @Field
    @Schema(description = "申请人姓名")
    private String applicantName;

    /** 联系电话 */
    @Field
    @Schema(description = "联系电话")
    private String applicantPhone;

    /** 身份证号 */
    @Field
    @Schema(description = "身份证号")
    private String idCard;

    /** 居住地址 */
    @Field
    @Schema(description = "居住地址")
    private String address;

    /** 家庭情况说明 */
    @Field
    @Schema(description = "家庭情况说明")
    private String familySituation;

    /** 帮扶类型：living生活困难 medical医疗困难 education教育困难 employment就业困难 */
    @Field
    @Schema(description = "帮扶类型")
    private String assistanceType;

    /** 困难描述 */
    @Field
    @Schema(description = "困难描述")
    private String difficultyDesc;

    /** 期望帮扶内容 */
    @Field
    @Schema(description = "期望帮扶内容")
    private String desiredHelp;

    /** 备注 */
    @Field
    @Schema(description = "备注")
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
        return applyId;
    }

    @Override
    public void setID(Long id) {
        this.applyId = id;
    }
}
