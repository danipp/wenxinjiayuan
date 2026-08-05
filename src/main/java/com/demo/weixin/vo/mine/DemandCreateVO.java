package com.demo.weixin.vo.mine;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

/**
 * 需求发布入参
 */
@Data
@Schema(description = "需求发布入参")
public class DemandCreateVO {

    @Schema(description = "需求标题")
    @NotBlank(message = "需求标题不能为空")
    private String title;

    @Schema(description = "需求详细内容")
    @NotBlank(message = "需求内容不能为空")
    private String content;

    @Schema(description = "服务地点")
    private String location;

    @Schema(description = "服务时间")
    private Date serviceTime;

    @Schema(description = "时间模式：negotiate双方协商 specific指定时间")
    private String timeType;

    @Schema(description = "指定时间文本（如：明天 上午09:00-12:00），timeType=specific时必填")
    private String specificTime;

    @Schema(description = "需求类型（如：代购、陪护、维修等）")
    private String requirement;

    @Schema(description = "服务对象姓名")
    private String memberName;

    @Schema(description = "服务对象电话")
    private String memberPhone;

    @Schema(description = "服务对象地址")
    private String memberAddress;

    @Schema(description = "服务对象详细门牌号")
    private String memberDetailAddress;

    // [新增 2026-08-03 17:40] 社区数据隔离字段
    @Schema(description = "所属社区ID（数据隔离用）")
    private Long communityId;

    @Schema(description = "备注（其他说明，300字以内）")
    private String remark;
}
