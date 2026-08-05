package com.demo.common.core.util;

import com.aliyuncs.utils.StringUtils;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 短信宝
 */
@Slf4j
public class SmsBaoUtil {

	public static final String template_VAL = "https://api.smsbao.com/sms?u=13922244170&p=1dcdc7b7c82e481a8eb814c5eba30b65&m=mobile&c=【sign】您的验证码是code。如非本人操作，请忽略本短信";
	
	public static final String template_VAL2 ="https://api.smsbao.com/sms?u=13922244170&p=1dcdc7b7c82e481a8eb814c5eba30b65&m=mobile&c=【广东高教网络】系统告警：code 请及时登录后台查看处理。";

	public static final String template_GENERAL = "https://api.smsbao.com/sms?u=13922244170&p=1dcdc7b7c82e481a8eb814c5eba30b65&m=mobile&c=content";
	
 
	
//	public static void main(String[] args) {
//        String phoneNumber = "13922244170";
//        String state = "login";
//        // 校验是否为手机号（中国）
//        boolean isMobile = Validator.isMobile(phoneNumber);
//        System.out.println(phoneNumber + " 是有效的中国手机号吗？ " + isMobile);
//        SmsBaoUtil.sendSMS(phoneNumber, "ERROR_SH","友米",state);
//        SmsBaoUtil.sendSMS(phoneNumber, "ERROR_LK","广东高教",state);
//    }
	
	
	
	
//	public static boolean sendSMS(String phoneNo, String code) {
//		final String sign = "广东高教网络";
//		final String state = "login";
//		return sendSMS(phoneNo,code,sign,state);
//	}
	
	public static final String STATE_GENERAL= "GENERAL";
	
	/**
	 * @param phoneNo 目标手机号
	 * @param code 验证码， 为空的时候用万能模板，否则用验证码专用模板
	 * @param sign 签名， 或者 要发送的短信的主题内容
	 * @param state 场景值，内部判断用。 GENERAL 表示通用模板.
	 * @return
	 */
	public static boolean sendSMS(String phoneNo, String code, String sign,String state) {
		String url  = null;
		if(StringUtils.isEmpty(code) || STATE_GENERAL.equalsIgnoreCase(state)) {
			//通用万能通道
			url  = template_VAL2.replace("mobile", phoneNo);
			url = url.replace("code", code);
//			url = url.replace("content", sign);
		}else {
			//验证码专用通道..
			url  = template_VAL.replace("mobile", phoneNo);
			url = url.replace("code", code);
			url = url.replace("sign", sign);
		} 
		 
		log.info(url);
		String result = HttpUtil.get(url, CharsetUtil.CHARSET_UTF_8);
		log.info(result);
		return "0".equalsIgnoreCase(result);
	}
}
