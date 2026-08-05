package com.demo.weixin.vo.notice;

import com.demo.weixin.vo.BaseQueryVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知分页查询入参
 * [新增 2026-08-03 19:30]
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "通知分页查询入参")
public class NoticeQueryVO extends BaseQueryVo {

    @Schema(description = "通知类型：1系统公告 2社区活动 3捐赠播报 4帮扶动态")
    private Integer type;

    @Schema(description = "状态：1上架 2下架")
    private Integer status;

    @Schema(description = "所属社区ID")
    private Long communityId;

    @Schema(description = "关键词（模糊搜索标题）")
    private String keyword;
}
