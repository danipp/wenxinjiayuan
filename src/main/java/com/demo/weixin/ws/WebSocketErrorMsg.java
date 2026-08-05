package com.demo.weixin.ws;

import lombok.Data;

@Data
public class WebSocketErrorMsg {

	private int code = 100;
	private String msg = "OK";
	
	public WebSocketErrorMsg() {}
	
	public WebSocketErrorMsg(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	
}
