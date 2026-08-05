package com.demo.weixin.ws;

/**
 * ws 事件处理器;事件驱动模型的   业务处理逻辑
 */
public interface WebsocketEventProcesser {

	/**
	 * 当收到来自客户端的事件时，服务端要如何处理？处理逻辑在这个接口里。 
	 * @param event  收到的事件消息对象
	 * @param wsUser  客户端的当前登录的业务用户(代表业务上的客户信息)
	 */
	public void onEvent(WebSocketEventMsg event,WsUser wsUser);
}
