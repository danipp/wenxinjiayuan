package com.demo.weixin.entity.store;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 用户积分账户实体
 * 独立于User实体，避免修改已有公共代码。
 * 每个用户对应一条积分记录，记录可用余额、冻结积分和累计统计。
 */
@Data
@NoArgsConstructor
@Document(collection = "userPoints")
@Schema(description = "用户积分账户")
public class UserPoints extends Base {

    /** 积分账户业务主键 */
    @Field
    private Long userPointsId;

    /** 用户ID（关联User.userId） */
    @Field
    @Schema(description = "用户ID")
    private Long userId;

    /** 当前可用积分余额 */
    @Field
    @Schema(description = "可用积分余额")
    private Integer balance;

    /** 冻结积分（下单未支付时冻结） */
    @Field
    @Schema(description = "冻结积分")
    private Integer frozenBalance;

    /** 累计获得积分 */
    @Field
    @Schema(description = "累计获得积分")
    private Integer totalEarned;

    /** 累计消耗积分 */
    @Field
    @Schema(description = "累计消耗积分")
    private Integer totalSpent;

    @Override
    public Long getID() {
        return userPointsId;
    }

    @Override
    public void setID(Long id) {
        this.userPointsId = id;
    }
}
