package com.demo.weixin.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import cn.hutool.core.convert.ConvertException;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseWebSocketHandler extends TextWebSocketHandler{
	
	public static String PING = "PING";

    /**
     * KEY ： 业务ID String型； VALUE:  ws会话列表
     */
    protected  final Map<Long,List<WebSocketSession>> bidSessions = new ConcurrentHashMap<>();
    
    /**
     * KEY：WebSocketSession的id，会话ID ；    VALUE: 业务ID，String型
     */
    protected final Map<String,Long> idsMap = new ConcurrentHashMap<>();
    
    /**
     * KEY：业务ID，String型; VALUE: WsUser
     */
    protected final Map<Long,WsUser> bidWsUserMap = new ConcurrentHashMap<>();
    
    Map<String ,WebsocketEventProcesser> processorMap = new HashMap<String, WebsocketEventProcesser>();
    
    
    public BaseWebSocketHandler() {
    	this.registEventProcessor(PING, new WebsocketEventProcesser() {
			
			@Override
			public void onEvent(WebSocketEventMsg event, WsUser wsUser) {
				 try {
					sendMessage(wsUser.getPartnerId(), new WebSocketEventMsg("PONG","PING-PONG"));
				} catch (IOException e) {
					log.error(e.getMessage(),e);
				}
			}
		});
    	
    	
    	this.doRegistEventProcessor();
    	log.info("#### 注册了WS事件名列表:{}",processorMap.keySet());
    }
    
    
    public void registEventProcessor(String eventName,WebsocketEventProcesser handler) {
    	processorMap.put(eventName, handler);
    }
    

    /**
     * 抽象方法： 请调用父类的 registEventProcessor (String eventName,WebsocketEventHandler handler) 方法 注册业务级别的事件处理器...
     * @see WebSocketEventMsg
     */
    abstract void doRegistEventProcessor();
    
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    	Long bizId = null;
        Map<String, Object> attributes = session.getAttributes();
        WsUser wsUser = (WsUser) attributes.get(WebSocketInterceptor.WS_PARAM_KEY_USER);
        if(wsUser == null) {
        	log.warn("新连接WS建联失败，内部错误，ERROR (wsUser == null)");
        	session.close();
        	return;
        }else {
        	 bizId = wsUser.getPartnerId();
             bidWsUserMap.put(bizId, wsUser);
             log.info("新连接建立OK: " + session.getId() + ",wsUser=" + JSONUtil.toJsonStr(wsUser));
        }
        if(wsUser == null || bizId == null) {
        	session.sendMessage(new TextMessage("校验失败！参数错误,非安全链接~"));
        	log.warn("新连接WS建联失败，内部错误，ERROR (bizId == null)");
        	session.close();
        }else {
        	List<WebSocketSession> oldSessionsList = bidSessions.get(bizId);
        	if(oldSessionsList == null) {
        		oldSessionsList = new ArrayList<WebSocketSession>();
        	}
        	oldSessionsList.add(session);
        	bidSessions.put(bizId, oldSessionsList);
        	idsMap.put(session.getId(), bizId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String sessionId = session.getId();
    	Long bizId = idsMap.get(sessionId);
    	WsUser wsUser = bidWsUserMap.get(bizId);
        log.info("收到消息: " + payload + "，来自会话: " + session.getId() + ",bizID="+bizId  + ",wsUser=" + wsUser.toString());
        WebSocketEventMsg event = null;
        String eventName = null;
        try {
        	event = JSONUtil.toBean(payload, WebSocketEventMsg.class);
        	eventName  = event.getEventName();
        	if(StringUtils.isEmpty(eventName)) {
        		//客户端参数错误 缺少事件名
        		log.error("客户端参数错误 缺少事件名");
        		sendMessage(session,WebSocketEventMsg.ERROR_NO_EVENT_NAME);
        	}
        	WebsocketEventProcesser processor = processorMap.get(eventName);
        	if(processor == null) {
        		log.error("服务端 缺少处理器定义:eventName={}",eventName);
        		//服务端 缺少处理器定义
        		sendMessage(session,WebSocketEventMsg.ERROR_NO_PROCESSER_REGISTED);
        	}else {
        		try {
        			processor.onEvent(event,wsUser);
        		}catch(Exception ex) {
        			log.error("WS 事件业务处理异常:" + ex.getMessage(),ex);
        		}
        	}
        }catch(ConvertException ex) {
        	log.error("无法转化消息为JSON对象: payload=" + payload);
        	sendMessage(session,WebSocketEventMsg.ERROR_JSON_FORMAT);
        }catch(Exception ex) {
        	log.error("handler 内部错误:payload=" + payload);
        	log.error(ex.getMessage(),ex);
        	sendMessage(session,WebSocketEventMsg.ERROR_SERVER_SIDE_EXCEPTION);
        }
    }
    
    
    public void sendMessage(WebSocketSession session, WebSocketEventMsg event) throws IOException {
    	String json = JSONUtil.toJsonPrettyStr(event);
    	if(session.isOpen()) {
    		session.sendMessage(new TextMessage(json));
    	}else {
    		log.warn("session closed, ignore event: {}",event);
    	}
    	
    }
    
    
    public void sendMessage(Long bizId, WebSocketEventMsg event) throws IOException {
//    	WebSocketSession session = this.sessions.get(bizId);
    	List<WebSocketSession> sessionList = bidSessions.get(bizId);
    	if(sessionList == null || sessionList.isEmpty()) {
    		log.error("NO session for bizID: {}, event ignored :{}",bizId,event);
    	}else {
    		for(WebSocketSession session : sessionList) {
    			sendMessage(session,event);
    		}
    	}
    }
    

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
    	String id = session.getId();
    	Long bizId = idsMap.get(id);
    	if(bizId != null) {
    		List<WebSocketSession> sessionList = bidSessions.get(bizId);
    		if(sessionList != null) {
    			sessionList.remove(session);
    			if(sessionList.isEmpty()) {
    				bidSessions.remove(bizId);
        		}
    		}
    	}
    	idsMap.remove(id);
        log.info("响应连接关闭: {}; idsMap.size={}  ,sessions.keySet().size()={},sessions.values().size()={}", session.getId() , idsMap.size(),   bidSessions.keySet().size(), bidSessions.values().size() );
    }
}
