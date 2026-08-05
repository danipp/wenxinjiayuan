package com.demo.common.dingding;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.demo.common.core.util.IpUtil;
import com.demo.common.core.util.SpringBeanContextHolder;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
 
/**
 * @描述 异常告警钉钉通知
 * @创建人 caoju
 * @创建时间 2022/1/15 9:03
 */
@Slf4j
public class DingdingRobot {
    //这里就是刚才拿到的Webhook的值
    public static final String DING_DING_TOKEN_1 =
            "https://oapi.dingtalk.com/robot/send?access_token=48810bbd2e4445c82e1ea5c3bb20ffb9e5b58c94edda09a6fb33908e38b25e5a";
    
    public static final String DING_DING_TOKEN_NFC = "https://oapi.dingtalk.com/robot/send?access_token=b8686f4607e90a9a90979ee798c613be2a8ec86a63a0e93f63ec22319a018590";
 
    
    private static int c = 0;
    
    public static void sendDDMessageAsync(String msg){
    	ThreadUtil.execute(()->{
    		sendDDMessage(msg);
    	});
    }
    
    public static void sendDDMessage(String msg){
//    	if(SpringBeanContextHolder.isDevpEnv() || SpringBeanContextHolder.isTestEnv()) {
//    		if(c++  % 10 != 0) {
//    			//忽略，防止过多告警....
//    			return;
//    		}
//    	}
        String env = SpringBeanContextHolder.getEnv();
        String hostIP = IpUtil.getHostAddress();
        String time = DateUtil.now();
    	//钉钉的webhook
        //请求的JSON数据，这里用map在工具类里转成json格式
        Map<String,Object> json=new HashMap<>();
        Map<String,Object> text=new HashMap<>();
        json.put("msgtype","text");
        text.put("content","GFW："+hostIP +" - "+  env + "\r\n"  +msg + "\r\n"+ time);
        json.put("text",text);
        //发送post请求
        String response = SendHttps.sendPostByMap(DING_DING_TOKEN_1, json);
        log.info("项目告警发送钉钉，响应结果：{}",response);
    }
    
    public static void sendDDMessageMarkDown(String title, String markDownContent) {
    	 String requestJson = SendHttps.buildMarkdownJson(title, markDownContent, new ArrayList<String>(), false);
         //发送post请求
         boolean response = SendHttps.doPostRequest(DING_DING_TOKEN_NFC, requestJson);
         log.info("项目告警发送钉钉，响应结果：{}",response);
    }
    
    public static void sendDDMessageMarkDown1(String title, String markDownContent) {
   	 String requestJson = SendHttps.buildMarkdownJson(title, markDownContent, new ArrayList<String>(), false);
        //发送post请求
        boolean response = SendHttps.doPostRequest(DING_DING_TOKEN_1, requestJson);
        log.info("项目告警发送钉钉，响应结果：{}",response);
   }
    
    public static void sendDDTest() {
    	 // Markdown消息标题
        String title = "项目状态报告";
        
        // 正确的Markdown格式示例
        String text = "### 🚀 "+title+"\n\n" +
                     "**📋 项目名称**: 订单管理系统 v2.0\n\n" +
                     "**📊 当前状态**: ✅ 运行正常\n\n" +
                     "---\n\n" +
                     "**📈 今日数据统计**:\n\n" +
                     "- 📝 新增订单: **152** 笔\n" +
                     "- 💰 交易金额: **¥86,542** 元\n" +
                     "- 👥 活跃用户: **234** 人\n" +
                     "- ⚠️ 异常订单: **3** 笔\n\n" +
                     "---\n\n" +
                     "**🎯 最近操作记录**:\n\n" +
                     "1. 🔐 用户登录认证系统优化\n" +
                     "2. 📊 订单数据查询性能提升\n" +
                     "3. 📄 统计报表生成功能完善\n" +
                     "4. 🐛 修复支付接口超时问题\n\n" +
                     "---\n\n" +
                     "**🔔 重要提醒**:\n\n" +
                     "> ⏰ 系统将于今晚 **23:00-24:00** 进行维护升级\n\n" +
                     "> 📞 如有问题请联系技术支持: @13800138000\n\n" +
                     "---\n\n" +
                     "📅 时间: " + DateUtil.now() + "\n\n" +
                     "👤 报告人: GFW系统监控机器人";
        
        
        
        sendDDMessageMarkDown1(title, text);
    }
    
    
    public static void main(String[] args) {
        DingdingRobot.sendDDTest( );
    }

 
}
