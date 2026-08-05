package com.demo.weixin.interceptor;


import cn.hutool.json.JSONUtil;
import com.demo.common.core.result.IResultCode;
import com.demo.common.core.result.Result;
import com.demo.common.core.result.ResultCode;
import com.demo.weixin.annotation.NeedLogin;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.entity.User;
import com.demo.weixin.service.UserService;
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
public class UserLoginInterceptor implements HandlerInterceptor {

    @Resource
    private RedisTemplate<String, String> stringRedisTemplate;

    @Resource
    private UserService userService;

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
        if (method.isAnnotationPresent(NeedLogin.class)) {
            NeedLogin needLogin = method.getAnnotation(NeedLogin.class);
            if (needLogin.required()) {
                return handlerUserLoginCheck(request, response, needLogin);
            }
        }
        return true;
    }

    /**
     * 后台管理 子系统的校验
     *
     * @param request
     * @param response
     * @param needLogin 登录注解（含角色校验参数）
     * @return
     * @throws Exception
     */
    private boolean handlerUserLoginCheck(@NotNull HttpServletRequest request, HttpServletResponse response, NeedLogin needLogin) throws Exception {

        String token = request.getHeader(Constants.TOKEN);
        if (!StringUtils.hasText(token)) {
            resp(response, ResultCode.AUTH_FAILED);
            return false;
        }
        String userInfo = userInfo(token);
        User user = JSONUtil.toBean(userInfo, User.class);
        if (Objects.isNull(user) || Objects.isNull(user.getUserId())) {
            resp(response, ResultCode.USER_ERROR);
            return false;
        } else {
            boolean frozen = stringRedisTemplate.hasKey(Constants.USER_FROZEN + ":" + user.getUserId());
            if (frozen) {
                resp(response, ResultCode.AUTH_FORBIDDEN);
                return false;
            }
        }

        // ================== 角色级校验 ==================
        if (needLogin != null) {
            if (needLogin.isOwner() && !Boolean.TRUE.equals(user.getOwner())) {
                resp(response, "无商户权限");
                return false;
            }
            if (needLogin.isProxy() && !Boolean.TRUE.equals(user.getAgent())) {
                resp(response, "无代理权限");
                return false;
            }
            if (needLogin.isDeveloper() && !Boolean.TRUE.equals(user.getDeveloper())) {
                resp(response, "无开发者权限");
                return false;
            }
        }

        userService.setUserSession(token, user);
        request.setAttribute("CURRENT_LOGIN_USER", user);
        return true;

    }

    private String userInfo(String token) {
        return stringRedisTemplate.opsForValue().get(Constants.REDIS_SESSION_USER + ":" + token);
    }


    private void resp(HttpServletResponse response, IResultCode resultCode) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write(JSONUtil.toJsonStr(Result.failed(resultCode)));
        out.close();
    }

    /**
     * 返回自定义错误消息
     */
    private void resp(HttpServletResponse response, String msg) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write(JSONUtil.toJsonStr(Result.failed(msg)));
        out.close();
    }

}
