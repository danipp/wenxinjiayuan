package com.demo.weixin.vo.community;

import com.demo.weixin.vo.BaseQueryVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 社区查询入参
 * 管理员可查看全部社区，C端用户只查启用状态的社区。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "社区查询入参")
public class CommunityQueryVO extends BaseQueryVo {

    @Schema(description = "社区名称（模糊搜索）")
    private String keyword;

    @Schema(description = "状态筛选：1启用 2禁用（不传则查全部）")
    private Integer status;
}
