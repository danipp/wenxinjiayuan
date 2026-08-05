package com.demo.weixin.service;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 微信支付配置切换 服务实现类
 * </p>
 *
 * @author Zane
 */
@Service
public class WxPaySwitchService {

    @Autowired
    @Qualifier("wxServiceMchConfig")
    private WxPayConfig serviceMchConfig;

    @Autowired
    @Qualifier("wxMerchantConfig")
    private WxPayConfig merchantConfig;

    @Autowired
    private WxPayService wxPayService;

    public WxPayService merchantPayService() {
        return wxPayService.switchoverTo(merchantConfig.getMchId(), merchantConfig.getAppId());
    }

    public WxPayService servicePayService() {
        return wxPayService.switchoverTo(serviceMchConfig.getMchId(), serviceMchConfig.getAppId());
    }

}
