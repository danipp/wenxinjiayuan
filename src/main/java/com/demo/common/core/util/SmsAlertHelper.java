package com.demo.common.core.util;

import com.demo.common.dingding.DingdingRobot;

public abstract class SmsAlertHelper {
	/**
	 * 错误计数器
	 */
	private  int error_count = 0;
	/**
	 * 上次短信告警时间
	 */
	private  long lastSendSMS = 0;
	/**
	 * 最小的短信发送间隔
	 */
	private   long min_interval_send_sms = 1 * 60 * 1000;//1分钟 
	
	/**
	 * 错误计数器和短信报警逻辑
	 * @param state 场景
	 * @param ex 可能是null
	 */
	protected void exProcessAndAlert(String state, Exception ex) {
		String exMsg = "";
		if(ex != null) {
			exMsg = ex.getClass().getSimpleName().concat(" : ").concat(""+ex.getMessage());
		}
		if(error_count % 3 == 0 && ((System.currentTimeMillis() - lastSendSMS) > min_interval_send_sms)) {
//			SMS 报警
//			SmsUtil.sendSMS(SmsUtil.WARNING_phoneNumber, state + error_count, "" ,SmsBaoUtil.STATE_GENERAL);
			//钉钉告警
			DingdingRobot.sendDDMessageAsync(state + "\r\n" + exMsg + "\r\nerror_count="+error_count);
		}
		error_count++;
	}

	
	 
}
