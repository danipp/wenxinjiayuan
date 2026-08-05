package com.demo.weixin.conf;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Configuration
@Slf4j
public class WxPayConfiguration {

    @Autowired
    @Qualifier("wxServiceMchConfig")
    private WxPayConfig serviceMchConfig;

    @Autowired
    @Qualifier("wxMerchantConfig")
    private WxPayConfig merchantConfig;

    @Bean
    public WxPayService wxPayService() {
        WxPayService service = new WxPayServiceImpl();
        service.setConfig(serviceMchConfig);
        service.addConfig(merchantConfig.getMchId(),merchantConfig.getAppId(),merchantConfig);
        return service;
    }
}
