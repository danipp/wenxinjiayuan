package com.demo.weixin.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式高并发防重入幂等锁
 * 采用 Redisson 看门狗自动续期机制，解决第三方调用时间不确定问题
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedIdempotent {

    /**
     * 锁的 Key 命名空间前缀 (如 "lock:lucky:draw")
     */
    String prefix() default "lock:idempotent";

    /**
     * 支持 SpEL 表达式的动态 Key (如 "#vo.merchantId" 或 "#recordId")
     * 如果为空，默认采用 "前缀:当前用户ID:类名:方法名" 进行自动物理隔离
     */
    String key() default "";

    /**
     * 获取锁的最大等待时间 (默认 0 秒，即获取不到锁立即返回“请勿重复提交”，防高频连击)
     */
    long waitTime() default 0;

    /**
     * 等待时间的单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 拦截后的报错提示文案
     */
    String message() default "正在火速处理中，请不要高频连击";
}