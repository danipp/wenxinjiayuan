package com.demo.weixin.interceptor;

import cn.hutool.json.JSONUtil;
import com.demo.common.core.result.IResultCode;
import com.demo.common.core.result.Result;
import com.demo.common.core.result.ResultCode;
import com.demo.weixin.annotation.AdminNeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.entity.Admin;
import com.demo.weixin.service.AdminService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 登录拦截
 */
@Slf4j
public class AdminLoginInterceptor implements HandlerInterceptor {

    @Resource
    private RedisTemplate<String, String> stringRedisTemplate;

    @Resource
    private AdminService adminService;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object object) throws Exception {
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        String uri = request.getRequestURI();
        log.info(">> uri={}, method={}", uri, method.getName());
        if (method.isAnnotationPresent(AdminNeedLogin.class)) {
            AdminNeedLogin needLogin = method.getAnnotation(AdminNeedLogin.class);
            if (needLogin.required()) {
                return handlerUserLoginCheck(request, response);
            }
        }
        return true;
    }

    /**
     * 普通用户管理子系统的校验...
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    private boolean handlerUserLoginCheck(@NotNull HttpServletRequest request, HttpServletResponse response) throws Exception {

        String token = request.getHeader(Constants.TOKEN_ADMIN);
        if (!StringUtils.hasText(token)) {
            resp(response, ResultCode.AUTH_FAILED);
            return false;
        }
        String userInfo = userInfo(token);
        Admin user = JSONUtil.toBean(userInfo, Admin.class);
        if (Objects.isNull(user) || Objects.isNull(user.getAdminId())) {
            resp(response, ResultCode.AUTH_FAILED);
            return false;
        } else {
            boolean frozen = stringRedisTemplate.hasKey(Constants.ADMIN_USER_FROZEN + ":" + user.getAdminId());
            if (frozen) {
                resp(response, ResultCode.AUTH_FORBIDDEN);
                return false;
            }
        }
        adminService.setAdminSession(token, user);
        request.setAttribute("CURRENT_LOGIN_ADMIN", user);

        return true;

    }

    private String userInfo(String token) {
        return stringRedisTemplate.opsForValue().get(Constants.REDIS_SESSION_ADMIN + ":" + token);
    }

    private void resp(HttpServletResponse response, IResultCode resultCode) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write(JSONUtil.toJsonStr(Result.failed(resultCode)));
        out.close();
    }

}
