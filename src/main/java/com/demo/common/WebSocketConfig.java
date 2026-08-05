package com.demo.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.demo.weixin.ws.WebSocketHandler;
import com.demo.weixin.ws.WebSocketInterceptor;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {
 
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//    	log.info("prefix={}",prefix);
    	
        registry.addHandler(WebSocketHandler.getWebSocketHandler(),  "/ws" )
        		.addInterceptors(new WebSocketInterceptor()) // 添加拦截器
                .setAllowedOrigins("*");
        log.info("registerWebSocketHandlers OK");
    }
}