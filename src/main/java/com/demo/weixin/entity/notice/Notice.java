package com.demo.weixin.entity.notice;

import com.demo.weixin.entity.Base;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * 首页消息轮播通知实体
 * [新增 2026-08-03 19:30] 用于首页文字滚动通知（如系统公告、社区动态、捐赠播报等）
 * 管理端维护通知内容，C端按社区+有效期+上架状态查询，前端做滚动轮播展示。
 */
@Data
@NoArgsConstructor
@Document(collection = "notice")
@Schema(description = "消息通知")
public class Notice extends Base {

    /** 通知业务主键 */
    @Field
    private Long noticeId;

    /** 通知标题（简短摘要，用于轮播展示） */
    @Field
    @Schema(description = "通知标题")
    private String title;

    /** 通知内容（完整内容，点击可查看详情） */
    @Field
    @Schema(description = "通知内容")
    private String content;

    /** 通知类型：1系统公告 2社区活动 3捐赠播报 4帮扶动态 */
    @Field
    @Schema(description = "通知类型：1系统公告 2社区活动 3捐赠播报 4帮扶动态")
    private Integer type;

    /** 所属社区ID（null表示全局通知，所有社区可见） */
    @Field
    @Schema(description = "所属社区ID（null表示全局通知）")
    private Long communityId;

    /** 所属社区名称（冗余字段，减少跨表查询） */
    @Field
    @Schema(description = "所属社区名称")
    private String communityName;

    /** 跳转类型：0不跳转 1活动 2商品 3店铺 4外部链接 */
    @Field
    @Schema(description = "跳转类型：0不跳转 1活动 2商品 3店铺 4外部链接")
    private Integer linkType;

    /** 跳转目标值（活动ID/商品ID/店铺ID/外部URL） */
    @Field
    @Schema(description = "跳转目标值")
    private String linkValue;

    /** 排序号（越小越靠前） */
    @Field
    @Schema(description = "排序号")
    private Integer sortNum;

    /** 状态：1上架 2下架 */
    @Field
    @Schema(description = "状态：1上架 2下架")
    private Integer status;

    /** 投放开始时间（null表示立即生效） */
    @Field
    @Schema(description = "投放开始时间")
    private Date startTime;

    /** 投放结束时间（null表示长期有效） */
    @Field
    @Schema(description = "投放结束时间")
    private Date endTime;

    @Override
    public Long getID() {
        return noticeId;
    }

    @Override
    public void setID(Long id) {
        this.noticeId = id;
    }
}
