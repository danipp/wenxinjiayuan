package com.demo.weixin.vo.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * 通知保存入参（新增/编辑共用）
 * [新增 2026-08-03 19:30]
 */
@Data
@Schema(description = "通知保存入参")
public class NoticeSaveVO {

    @Schema(description = "通知ID（编辑时必传，新增时不传）")
    private Long noticeId;

    @Schema(description = "通知标题（简短摘要，用于轮播展示）")
    @NotBlank(message = "通知标题不能为空")
    private String title;

    @Schema(description = "通知内容（完整内容，点击可查看详情）")
    private String content;

    @Schema(description = "通知类型：1系统公告 2社区活动 3捐赠播报 4帮扶动态")
    @NotNull(message = "通知类型不能为空")
    private Integer type;

    @Schema(description = "所属社区ID（null表示全局通知，所有社区可见）")
    private Long communityId;

    @Schema(description = "跳转类型：0不跳转 1活动 2商品 3店铺 4外部链接")
    private Integer linkType;

    @Schema(description = "跳转目标值（活动ID/商品ID/店铺ID/外部URL）")
    private String linkValue;

    @Schema(description = "排序号（越小越靠前）")
    private Integer sortNum;

    @Schema(description = "状态：1上架 2下架")
    private Integer status;

    @Schema(description = "投放开始时间（null表示立即生效）")
    private Date startTime;

    @Schema(description = "投放结束时间（null表示长期有效）")
    private Date endTime;
}
