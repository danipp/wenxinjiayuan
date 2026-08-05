package com.demo.weixin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 登录校验注解。
 * 可通过 isOwner/isProxy/isDeveloper 参数指定角色级校验，
 * 拦截器会检查当前登录 User 对应的标志位是否为 true。
 *
 * @author JT
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NeedLogin {
    boolean required() default true;

    /** 是否要求商户角色 */
    boolean isOwner() default false;

    /** 是否要求代理角色 */
    boolean isProxy() default false;

    /** 是否要求开发者角色 */
    boolean isDeveloper() default false;
}
