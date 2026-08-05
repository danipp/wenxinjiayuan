package com.demo.weixin.annotation;

import java.lang.annotation.*;

/**
 * @author zane
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelAlias {

    String value() default "";

}

