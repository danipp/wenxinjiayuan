package com.demo.weixin.aspect;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.demo.common.core.util.IpUtil;
import com.demo.common.exception.BizException;
import com.demo.weixin.annotation.DistributedIdempotent;
import com.demo.weixin.controller.BaseController;
import com.demo.weixin.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class DistributedIdempotencyAspect {

    @Autowired
    private RedissonClient redissonClient;

    // 解析 SpEL 表达式的工具
    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(idempotent)")
    public Object doAround(ProceedingJoinPoint joinPoint, DistributedIdempotent idempotent) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 1. 解析分布式锁 Key
        String lockKey = buildLockKey(joinPoint, method, idempotent);
        
        RLock lock = redissonClient.getLock(lockKey);
        boolean isAcquired = false;

        try {
            // 2. 尝试抢锁 (LeaseTime 必须传入 -1 以强制激活 Redisson 自动续期看门狗机制)
            isAcquired = lock.tryLock(idempotent.waitTime(), -1, idempotent.timeUnit());
            
            if (!isAcquired) {
                // 抢锁失败，直接抛出业务异常，由全局异常处理器统一拦截成 Result.failed() 返回前端
                throw new BizException(idempotent.message());
            }

            log.info("🔑 成功抢占分布式防重入锁，Key: {}", lockKey);
            
            return joinPoint.proceed();

        } finally {
            // 4. 释放锁资源 (只有持有锁的当前线程才能解锁，防止误解锁)
            if (isAcquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("🔓 业务执行完毕，原子释放分布式锁，Key: {}", lockKey);
            }
        }
    }

    /**
     * 动态组装 Key，支持 SpEL 表达式
     */
    private String buildLockKey(ProceedingJoinPoint joinPoint, Method method, DistributedIdempotent idempotent) {
        StringBuilder sb = new StringBuilder(idempotent.prefix());
        Long userId = null;
        User loginUser = null;
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                loginUser = (User) request.getAttribute("CURRENT_LOGIN_USER");
                if (loginUser != null) {
                    userId = loginUser.getUserId();
                }
            }
        } catch (Exception e) {
            log.error("幂等锁切面获取上下文用户ID失败", e);
        }

        // 2. 初始化 SpEL 解析上下文
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 3. 注入 Java 方法中声明的真实形参
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = discoverer.getParameterNames(method);
        if (parameterNames != null && parameterNames.length > 0) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        if (userId != null) {
            // 将 userId 和 user 对象作为虚拟变量注入到 SpEL 容器中
            context.setVariable("userId", userId);
            context.setVariable("user", loginUser);
        }

        // A. 如果配置了 SpEL 表达式，解析参数值
        if (StrUtil.isNotBlank(idempotent.key())) {
            try {
                // 此时 SpEL 容器中既有方法的入参，又有我们刚刚手动塞入的 #userId 和 #user 变量！
                Object val = parser.parseExpression(idempotent.key()).getValue(context);
                if (val != null) {
                    sb.append(":").append(val);
                }
            } catch (Exception e) {
                log.error("SpEL 表达式解析失败，表达式: {}", idempotent.key(), e);
            }
        } else {
            // B. 默认防重兜底机制：前缀 + 隔离标识(用户ID/匿名指纹) + 完整的包路径类名 + 方法名
            if (userId != null) {
                // 1. 已登录：使用当前用户 ID 隔离
                sb.append(":user:").append(userId);
            } else {
                // 2. 未登录 (极端防御)：通过 IP + UA 动态提取匿名设备指纹，防止未登录接口全局锁死
                try {
                    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    if (attributes != null) {
                        HttpServletRequest request = attributes.getRequest();
                        String ip = IpUtil.getRequestIp(request); // 沿用项目自带Ip工具
                        String ua = request.getHeader("user-agent");
                        String fingerprint = cn.hutool.crypto.SecureUtil.md5(ip + "_" + (ua != null ? ua : ""));
                        sb.append(":fingerprint:").append(fingerprint);
                    }
                } catch (Exception e) {
                    // 如果获取 Request 失败，采用随机 UUID 保证本次请求不与任何人碰撞
                    sb.append(":anonymous:").append(IdUtil.fastSimpleUUID());
                }
            }
            // 结果形如：com.demo.weixin.controller.LuckyWheelUserController:executeDraw
            sb.append(":").append(joinPoint.getTarget().getClass().getName())
                    .append(":").append(method.getName());
        }
        
        return sb.toString();
    }
}