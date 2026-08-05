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
 * 物资申领实体
 * 记录用户对帮扶物资的申领申请及审核、发放状态。
 */
@Data
@NoArgsConstructor
@Document(collection = "goodsClaim")
@Schema(description = "物资申领")
public class GoodsClaim extends Base {

    /** 物资申领业务主键 */
    @Field
    private Long claimId;

    /** 申领用户ID */
    @Indexed
    @Field
    @Schema(description = "申领用户ID")
    private Long userId;

    /** 商品ID */
    @Indexed
    @Field
    @Schema(description = "商品ID")
    private Long goodsId;

    /** 商品标题（冗余） */
    @Field
    @Schema(description = "商品标题")
    private String goodsTitle;

    /** 商品图片（冗余） */
    @Field
    @Schema(description = "商品图片")
    private String goodsImage;

    /** 申领数量 */
    @Field
    @Schema(description = "申领数量")
    private Integer claimCount;

    /** 申领原因 */
    @Field
    @Schema(description = "申领原因")
    private String claimReason;

    /** 联系人 */
    @Field
    @Schema(description = "联系人")
    private String contactName;

    /** 联系电话 */
    @Field
    @Schema(description = "联系电话")
    private String contactPhone;

    /** 收货地址 */
    @Field
    @Schema(description = "收货地址")
    private String address;

    /** 申领状态 */
    @Field
    @Schema(description = "申领状态")
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
        return claimId;
    }

    @Override
    public void setID(Long id) {
        this.claimId = id;
    }
}
