package com.demo.weixin.ws;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;

@Data
public class WebSocketEventMsg {

	String eventName;
	
	String eventData;
	
	String eventDataClass;
	
	String eventTime;
	
	
	public WebSocketEventMsg() {}
	
	
	public WebSocketEventMsg(String eventName, Object data) {
		this.eventName = eventName;
		if(data instanceof String) {
			this.eventData = (String)data;
			this.eventDataClass = String.class.getName();
		}else {
			this.eventData = JSONUtil.toJsonStr(data);
			this.eventDataClass = data.getClass().getName();
		}
		
		this.eventTime = DateUtil.now();
	}
	
 
	
	public static final  WebSocketEventMsg ERROR_NO_EVENT_NAME = new WebSocketEventMsg("ERROR",new WebSocketErrorMsg(404,"event name not exist"));
	
	public static final  WebSocketEventMsg ERROR_JSON_FORMAT = new WebSocketEventMsg("ERROR",new WebSocketErrorMsg(403,"json format error"));
	
	public static final  WebSocketEventMsg ERROR_NO_PROCESSER_REGISTED = new WebSocketEventMsg("ERROR",new WebSocketErrorMsg(500,"no processor for this  event name"));
	
	public static final  WebSocketEventMsg ERROR_SERVER_SIDE_EXCEPTION= new WebSocketEventMsg("ERROR",new WebSocketErrorMsg(502,"server side exception"));
	
	
 
}
