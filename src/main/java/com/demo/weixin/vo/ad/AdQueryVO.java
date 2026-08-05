package com.demo.weixin.vo.ad;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 广告位查询入参（管理端）
 */
@Data
@Schema(description = "广告位查询入参")
public class AdQueryVO {

    @Schema(description = "广告位标识（可选筛选）")
    private String position;
}
