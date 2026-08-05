package com.demo.weixin.entity.assistance;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

/**
 * 爱心企业实体
 * 记录参与消费帮扶的爱心企业信息及累计捐赠数据。
 */
@Data
@NoArgsConstructor
@Document(collection = "charityEnterprise")
@Schema(description = "爱心企业")
public class CharityEnterprise extends Base {

    /** 爱心企业业务主键 */
    @Field
    private Long enterpriseId;

    /** 企业名称 */
    @Field
    @Schema(description = "企业名称")
    private String name;

    /** 企业Logo URL */
    @Field
    @Schema(description = "企业Logo URL")
    private String logo;

    /** 企业简介 */
    @Field
    @Schema(description = "企业简介")
    private String description;

    /** 联系人 */
    @Field
    @Schema(description = "联系人")
    private String contactName;

    /** 联系电话 */
    @Field
    @Schema(description = "联系电话")
    private String contactPhone;

    /** 企业地址 */
    @Field
    @Schema(description = "企业地址")
    private String address;

    /** 累计捐赠金额 */
    @Field
    @Schema(description = "累计捐赠金额")
    private BigDecimal totalDonationAmount;

    /** 累计捐赠次数 */
    @Field
    @Schema(description = "累计捐赠次数")
    private Integer totalDonationCount;

    /** 状态：active已上线 inactive已下线 */
    @Field
    @Schema(description = "状态")
    private String status;

    /** 排序权重（越小越靠前） */
    @Field
    @Schema(description = "排序权重")
    private Integer sort;

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
        return enterpriseId;
    }

    @Override
    public void setID(Long id) {
        this.enterpriseId = id;
    }
}
