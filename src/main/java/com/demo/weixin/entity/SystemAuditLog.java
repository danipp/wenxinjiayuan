package com.demo.weixin.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 系统后台操作审计日志
 *
 * @author zane
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "systemAuditLog")
@Schema(description = "后台系统操作审计日志")
public class SystemAuditLog extends Base {

    @Field
    private Long id;

    @Schema(description = "操作人用户ID")
    private Long operatorId;

    @Schema(description = "操作人姓名/账号")
    private String operatorName;

    @Schema(description = "操作模块（如：点位管理、广告管理）")
    private String module;

    @Schema(description = "操作行为描述（如：修改WiFi密码、更新广告预算）")
    private String action;

    @Schema(description = "执行的Java方法名")
    private String method;

    @Schema(description = "请求URL")
    private String requestUrl;

    @Schema(description = "请求方式 (GET/POST/PUT/DELETE)")
    private String requestMethod;

    @Schema(description = "请求参数 (JSON格式)")
    private String requestParams;

    @Schema(description = "响应结果 (JSON格式)")
    private String responseResult;

    @Schema(description = "操作状态 (1:成功, 0:失败)")
    private Integer status;

    @Schema(description = "异常报错堆栈信息")
    private String errorMessage;

    @Schema(description = "客户端IP地址")
    private String ip;

    @Schema(description = "客户端浏览器User-Agent")
    private String userAgent;

    @Schema(description = "方法执行耗时(毫秒)")
    private Long costTime;

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public void setID(Long id) {
        this.id = id;
    }
}