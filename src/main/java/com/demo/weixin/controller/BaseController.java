package com.demo.weixin.controller;

import com.demo.common.exception.BizException;
import com.demo.weixin.constant.Constants;
import com.demo.weixin.entity.User;
import com.demo.weixin.enums.PlatformTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;


@Slf4j
public abstract class BaseController {

    @Autowired(required = false)
    protected HttpServletRequest request;

    @Autowired(required = false)
    protected HttpServletResponse response;

    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    protected User getUser() {
        // 直接从当前线程的 Request 域中无损提取
        return (User) request.getAttribute("CURRENT_LOGIN_USER");
    }

    protected Long getCurrentUserId() {
        User user = getUser();
        if (user == null) {
            log.warn("current user not login, or expired.");
            return null;
        }
        return user.getUserId();
    }

    protected Integer getPlatform() {
        String platform = request.getHeader(Constants.PLATFORM);
        return PlatformTypeEnum.getKeyByDesc(platform);
    }

    protected PlatformTypeEnum getPlatformEnum() {
        String platform = request.getHeader(Constants.PLATFORM);
        return PlatformTypeEnum.getEnumByDesc(platform);
    }


    protected String getPosId() {
        String posId = request.getHeader(Constants.POS_ID);
        if (!StringUtils.hasText(posId)) {
            return null;
        }
        //枚举。。。
        return posId;
    }

    protected String getWifiPointId() {
        String wifiPointId = request.getHeader(Constants.WIFI_ID);
        if (!StringUtils.hasText(wifiPointId)) {
        	log.warn("use default wifi point id. NO wifi-id in request HEAD @rosun");
            return "123456";
        }
        //枚举。。。
        return wifiPointId;
    }


    protected String getStoreId() {
        String storeId = request.getHeader(Constants.STORE_ID);
        if (!StringUtils.hasText(storeId)) {
            return null;
        }
        //枚举。。。
        return storeId;
    }


    /**
     * 类型  1派样 2 互动后
     * @return
     */
    protected Integer getGoodsType() {
        String goodsType = request.getHeader(Constants.GOODS_TYPE);
        if (!StringUtils.hasText(goodsType)) {
            return null;
        }
        //枚举。。。
        return Integer.valueOf(goodsType);
    }

    /**
     * 获取当前操作角色，用于多角色用户区分当前以哪个身份操作。
     * 前端通过 header "role" 传入：agent / developer / merchant。
     * 后端校验用户是否拥有该角色，返回内部角色标识（AGENT / DEVELOPER / MERCHANT）。
     *
     * @return 钱包角色标识：AGENT / DEVELOPER / MERCHANT
     */
    protected String getCurrentRole() {
        String role = request.getHeader(Constants.ROLE);
        if (!StringUtils.hasText(role)) {
            throw new BizException("缺少角色标识，请在请求头中传入role（agent/developer/merchant）");
        }
        User user = getUser();
        if (user == null) {
            throw new BizException("未登录");
        }
        switch (role.toLowerCase()) {
            case "agent":
                if (!Boolean.TRUE.equals(user.getAgent())) {
                    throw new BizException("当前账号无代理角色权限");
                }
                return "AGENT";
            case "developer":
                if (!Boolean.TRUE.equals(user.getDeveloper())) {
                    throw new BizException("当前账号无开发者角色权限");
                }
                return "DEVELOPER";
            case "merchant":
                if (!Boolean.TRUE.equals(user.getOwner())) {
                    throw new BizException("当前账号无商户角色权限");
                }
                return "MERCHANT";
            default:
                throw new BizException("无效的角色标识：" + role + "，应为 agent/developer/merchant");
        }
    }



}
