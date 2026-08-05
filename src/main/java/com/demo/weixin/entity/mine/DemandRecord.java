package com.demo.weixin.entity.mine;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * 需求/帮忙记录实体
 * 需求发布记录和帮忙记录共用本表，通过查询条件区分视角：
 * - 需求发布记录：publisherUserId = 当前用户
 * - 帮忙记录：helperUserId = 当前用户
 * 状态流转通过 DemandStatusEnum 管理。
 */
@Data
@NoArgsConstructor
@Document(collection = "demandRecord")
// 复合索引：发布者视角查询与帮忙者视角查询、排行榜聚合均依赖以下索引
@CompoundIndexes({
        @CompoundIndex(name = "idx_demand_publisher", def = "{'publisherUserId': 1, 'status': 1, 'del_flag': 1}"),
        @CompoundIndex(name = "idx_demand_helper", def = "{'helperUserId': 1, 'status': 1, 'del_flag': 1}")
})
@Schema(description = "需求/帮忙记录")
public class DemandRecord extends Base {

    /** 需求记录业务主键 */
    @Field
    private Long demandId;

    /** 发布者用户ID */
    @Field
    @Schema(description = "发布者用户ID")
    private Long publisherUserId;

    /** 帮忙者用户ID（接单前为null） */
    @Field
    @Schema(description = "帮忙者用户ID")
    private Long helperUserId;

    /** 需求标题 */
    @Field
    @Schema(description = "需求标题")
    private String title;

    /** 需求详细内容 */
    @Field
    @Schema(description = "需求内容")
    private String content;

    /** 服务地点 */
    @Field
    @Schema(description = "服务地点")
    private String location;

    /** 服务时间 */
    @Field
    @Schema(description = "服务时间")
    private Date serviceTime;

    /** 时间模式：negotiate双方协商 specific指定时间 */
    @Field
    @Schema(description = "时间模式：negotiate双方协商 specific指定时间")
    private String timeType;

    /** 指定时间文本（如：明天 上午09:00-12:00），timeType=specific时必填 */
    @Field
    @Schema(description = "指定时间文本")
    private String specificTime;

    /** 状态（对应 DemandStatusEnum.code） */
    @Field
    @Schema(description = "状态：1待帮忙 2已接单 3待评价 4已完成 5已过期")
    private Integer status;

    /** 需求类型（如：代购、陪护、维修等） */
    @Field
    @Schema(description = "需求类型")
    private String requirement;

    /** 服务对象姓名（冗余存储，不受ServiceMember变更影响） */
    @Field
    @Schema(description = "服务对象姓名")
    private String memberName;

    /** 服务对象电话（冗余存储） */
    @Field
    @Schema(description = "服务对象电话")
    private String memberPhone;

    /** 服务对象地址（冗余存储） */
    @Field
    @Schema(description = "服务对象地址")
    private String memberAddress;

    /** 服务对象详细门牌号（冗余存储） */
    @Field
    @Schema(description = "服务对象详细门牌号")
    private String memberDetailAddress;

    /** 备注（其他说明，300字以内） */
    @Field
    @Schema(description = "备注")
    private String remark;

    /** 视角标识：1发布者视角 2帮忙者视角。当前为保留字段，查询时按 publisherUserId/helperUserId 区分视角 */
    @Field
    @Schema(description = "视角：1发布者视角 2帮忙者视角（当前为保留字段，查询时按 publisherUserId/helperUserId 区分视角）")
    private Integer role;

    /** 评价评分（1-5，评价后才有值） */
    @Field
    @Schema(description = "评价评分：1-5")
    private Integer rating;

    /** 评价内容 */
    @Field
    @Schema(description = "评价内容")
    private String evaluateContent;

    /** 评价时间 */
    @Field
    @Schema(description = "评价时间")
    private Date evaluateTime;

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
        return demandId;
    }

    @Override
    public void setID(Long id) {
        this.demandId = id;
    }
}
