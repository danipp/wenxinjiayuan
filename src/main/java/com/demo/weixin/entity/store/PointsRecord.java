package com.demo.weixin.entity.store;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 积分流水记录实体
 * [新增 2026-08-03 19:10] 记录用户每一笔积分变动明细
 * 每次积分扣减/返还/增加时自动生成一条流水记录
 */
@Data
@NoArgsConstructor
@Document(collection = "pointsRecord")
@Schema(description = "积分流水记录")
public class PointsRecord extends Base {

    /** 流水记录业务主键 */
    @Field
    private Long recordId;

    /** 用户ID */
    @Field
    @Schema(description = "用户ID")
    private Long userId;

    /** 变动类型：1=获得，2=消耗，3=退还 */
    @Field
    @Schema(description = "变动类型：1=获得，2=消耗，3=退还")
    private Integer type;

    /** 变动数量（正数） */
    @Field
    @Schema(description = "变动数量（正数）")
    private Integer amount;

    /** 变动后余额 */
    @Field
    @Schema(description = "变动后余额")
    private Integer balanceAfter;

    /** 变动来源描述（如：签到奖励、商城下单、退款返还） */
    @Field
    @Schema(description = "变动来源")
    private String source;

    /** 关联订单ID（可选，商城交易时填入） */
    @Field
    @Schema(description = "关联订单ID")
    private Long relatedOrderId;

    /** 备注 */
    @Field
    @Schema(description = "备注")
    private String remark;

    @Override
    public Long getID() {
        return recordId;
    }

    @Override
    public void setID(Long id) {
        this.recordId = id;
    }
}
