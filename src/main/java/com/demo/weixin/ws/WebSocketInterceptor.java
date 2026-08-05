package com.demo.weixin.ws;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.demo.common.core.util.AesUtil;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketInterceptor implements HandshakeInterceptor {

	
//	public static final String WS_PARAM_KEY_ID = "id";
	public static final String WS_PARAM_KEY_TOKEN = "myToken";
	public static final String WS_PARAM_KEY_USER = "wsUser";
	
    /**
     * 握手前拦截，可以在这里获取参数、验证权限等
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            
            // 获取请求参数
//            String id = servletRequest.getServletRequest().getParameter(WS_PARAM_KEY_ID);
            String myToken = servletRequest.getServletRequest().getParameter(WS_PARAM_KEY_TOKEN);
            
            // 打印连接参数
            log.info("WS握手连接参数:  myToken-0: " + myToken);
            if (myToken == null) {
            	log.warn("WS握手拒绝连接：缺少myToken参数");
                return false;
            }
            try {
            	myToken = AesUtil.decrypt(myToken);
                log.info("WS握手连接参数:  myToken-1: " + myToken);
                // 将参数存入attributes，后续可以在WebSocketHandler中获取
            	attributes.put(WS_PARAM_KEY_TOKEN, myToken);
            	WsUser user  = JSONUtil.toBean(myToken, WsUser.class);
            	attributes.put(WS_PARAM_KEY_USER, user);
            }catch(Exception ex) {
            	log.error(ex.getMessage(),ex);
            	return false;
            }
        }
        return true;
    }

    /**
     * 握手后拦截
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
        	log.warn("WS握手后发生异常: " + exception.getMessage());
        } else {
        	log.info("WS握手成功完成");
        }
    }
}