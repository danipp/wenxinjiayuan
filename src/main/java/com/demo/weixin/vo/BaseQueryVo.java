package com.demo.weixin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author zane
 */

@Data
public class BaseQueryVo {

    @Schema(description = "页码，默认为1")
    private Integer pageNumber = 1;
    /**
     * 单页数据量，默认为20。
     * 注意：建议前端限制 pageSize 不超过 100，避免一次拉取过多数据导致性能问题。
     */
    @Schema(description = "单页数据量，默认为20")
    private Integer pageSize = 20;

}
