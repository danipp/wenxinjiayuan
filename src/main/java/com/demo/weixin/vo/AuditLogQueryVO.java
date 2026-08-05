package com.demo.weixin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@Schema(description = "系统操作日志查询入参")
public class AuditLogQueryVO extends BaseQueryVo{

    @Schema(description = "操作模块")
    private String module;

    @Schema(description = "操作人姓名")
    private String operatorName;

    @Schema(description = "操作状态 (1:成功, 0:失败)")
    private Integer status;

    @Schema(description = "开始时间")
    private Date startTime;

    @Schema(description = "结束时间")
    private Date endTime;
}