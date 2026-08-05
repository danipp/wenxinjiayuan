package com.demo.weixin.annotation;

import java.lang.annotation.*;

/**
 * 后台管理操作审计日志注解
 * 加上此注解的方法会自动异步记录操作日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ManageAuditLog {
    
    /**
     * 操作模块，例如："WiFi点位管理"、"广告投放"、"财务审批"
     */
    String module() default "";

    /**
     * 具体操作行为，例如："新增点位"、"审批提现"、"修改预算"
     */
    String action() default "";
}