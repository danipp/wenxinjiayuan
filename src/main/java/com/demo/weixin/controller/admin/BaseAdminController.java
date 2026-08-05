package com.demo.weixin.controller.admin;

import com.demo.weixin.entity.Admin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;


@Slf4j
public abstract class BaseAdminController {

    @Autowired(required = false)
    protected HttpServletRequest request;

    @Autowired(required = false)
    protected HttpServletResponse response;

    @Autowired
    protected RedisTemplate<String, String> stringRedisTemplate;

    protected Admin getAdmin() {
        return (Admin) request.getAttribute("CURRENT_LOGIN_ADMIN");
    }

    /**
     * 获取当前登录管理的id
     * @return
     */
    protected Long getCurrentAdminId() {
        Admin admin = getAdmin();
    	if(admin == null) {
    		log.warn("current admin not login, or expired.");
    		return null;
    	}else {
    		return admin.getAdminId();
    	}
    }
}
