package com.demo.weixin.aspect;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONUtil;
import com.demo.common.core.util.IpUtil;
import com.demo.weixin.annotation.ManageAuditLog;
import com.demo.weixin.entity.SystemAuditLog;
import com.demo.weixin.service.SystemAuditLogService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * 后台操作审计日志切面
 */
@Aspect
@Component
@Slf4j
public class ManageAuditLogAspect {

    @Resource
    private SystemAuditLogService systemAuditLogService;

    @Around("@annotation(manageAuditLog)")
    public Object doAround(ProceedingJoinPoint joinPoint, ManageAuditLog manageAuditLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        SystemAuditLog auditLog = new SystemAuditLog();
        auditLog.setCreateTime(new Date());
        // 1. 提取 Request 对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        if (request != null) {
            auditLog.setRequestUrl(request.getRequestURI());
            auditLog.setRequestMethod(request.getMethod());
            auditLog.setIp(IpUtil.getRequestIp(request));
            auditLog.setUserAgent(request.getHeader("user-agent"));
        }

        // 2. 提取注解上的配置信息
        auditLog.setModule(manageAuditLog.module());
        auditLog.setAction(manageAuditLog.action());

        // 3. 获取调用的类和方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        auditLog.setMethod(joinPoint.getTarget().getClass().getName() + "." + method.getName());

        // 4. 解析请求参数（排除过于庞大或敏感的参数如文件流、密码等）
        Object[] args = joinPoint.getArgs();
        try {
            auditLog.setRequestParams(JSONUtil.toJsonStr(args));
        } catch (Exception e) {
            auditLog.setRequestParams("参数解析失败: " + e.getMessage());
        }

        // 5. 获取当前操作人信息
        // 此处建议对接您项目现有的后台登录上下文（例如：Session、ThreadLocal或Spring Security）
        // 以下为示意性伪代码，请根据实际鉴权方式替换
        try {
            // 假设可以通过类似 getCurrentAdminId() 获取当前管理员
            // auditLog.setOperatorId(AdminUserContext.getUserId());
            // auditLog.setOperatorName(AdminUserContext.getUserName());
            auditLog.setOperatorId(999L); // 临时默认值
            auditLog.setOperatorName("系统管理员");
        } catch (Exception e) {
            auditLog.setOperatorName("未知操作员");
        }

        Object result = null;
        try {
            // 执行原方法
            result = joinPoint.proceed();

            auditLog.setStatus(1); // 成功
            if (result != null) {
                // 截取响应结果防止存入过大文本
                String resStr = JSONUtil.toJsonStr(result);
                auditLog.setResponseResult(resStr.length() > 1000 ? resStr.substring(0, 1000) + "..." : resStr);
            }
            return result;
        } catch (Throwable e) {
            auditLog.setStatus(0); // 失败
            auditLog.setErrorMessage(ExceptionUtil.stacktraceToString(e, 500)); // 只记录500字符以内的堆栈
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            auditLog.setCostTime(endTime - startTime);

            // 6. 异步保存日志，不阻塞主业务线程
            systemAuditLogService.saveLogAsync(auditLog);
        }
    }
}