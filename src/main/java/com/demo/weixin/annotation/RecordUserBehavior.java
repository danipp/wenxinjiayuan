package com.demo.weixin.annotation;

import java.lang.annotation.*;

/**
 * 记录用户WIFI广告行为日志
 *
 * @author zane
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RecordUserBehavior {

    /**
     * 行为类型 (如: SCAN_CODE, START_AD, FINISH_AD)
     */
    String actionType();

    /**
     * 页面来源
     */
    String page() default "HOME";
}