package com.demo.weixin.vo;

import com.demo.weixin.vo.BaseQueryVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 志愿者分页查询入参（管理员使用）
 * [新增 2026-08-03 21:00]
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "志愿者分页查询入参")
public class VolunteerQueryVO extends BaseQueryVo {

    @Schema(description = "志愿者状态：0=未激活 1=正常 2=停用")
    private Integer volunteerStatus;

    @Schema(description = "关键词（模糊搜索志愿者ID或昵称）")
    private String keyword;

    @Schema(description = "所属社区ID")
    private Long communityId;
}
