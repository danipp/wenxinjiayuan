package com.demo.weixin.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.common.core.result.Result;
import com.demo.common.core.util.IpUtil;
import com.demo.common.core.util.SnowflakeIdGenerator;
import com.demo.common.dingding.DingdingRobot;
import com.demo.weixin.dao.LandingPageH5LogDAO;
import com.demo.weixin.entity.LandingPageH5Log;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 落地页日志采集
 * POST接口， https://api.gaojiaomedia.cn/voucher-drop/api/landing/log
 *
 * @author  Rosun
 */
@RestController
@Tag(name = "H5落地页访问日志...")
@RequestMapping(value = "/api/landing")
@Slf4j
public class LandingPageH5Controller extends BaseController {
	
 
	
	/**
	 * K: 日期。  k:业务类型， v：访问计数。
	 */
	private Map<String,Map<String,Integer>> map  = new ConcurrentHashMap<>();
	
    @Autowired
    private LandingPageH5LogDAO landingPageLogDao;
    
    private static final String USER_ID_COOKIE = "ldpg_uuid";
	private static final int COOKIE_MAX_AGE = 365 * 24 * 60 * 60; // 365天
 

	private void writeUserIdCookie(String userId,HttpServletRequest request, HttpServletResponse response) {
		Cookie userIdCookie = new Cookie(USER_ID_COOKIE, userId);
		userIdCookie.setMaxAge(COOKIE_MAX_AGE); // 设置过期时间
		userIdCookie.setPath("/"); // 设置Cookie路径
		if("localhost".equals(request.getServerName()) || request.getServerName().contains("192.")) {
			userIdCookie.setHttpOnly(false); // 防止XSS攻击
			userIdCookie.setSecure(false); // 生产环境建议设为true(HTTPS)
		}else {
			userIdCookie.setHttpOnly(true); // 防止XSS攻击
			userIdCookie.setSecure(true); // 生产环境建议设为true(HTTPS)
		}
		// 添加到响应
		response.addCookie(userIdCookie);
	}

	private String getUserIdFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (USER_ID_COOKIE.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
	
    
    /**
     * 处理cookie 和用户数据
     * @param model
     * @return
     */
    protected String getUserId( String ip) {
    	String userId = this.getUserIdFromCookie(request);
		if(userId == null) {
			userId = SnowflakeIdGenerator.getInstance().nextId() + "";
			//新用户访问
			this.writeUserIdCookie(userId,request, response);
			log.info("新用户来了:{},{}",userId,ip);
		}else {
			//老用户回来了
			log.info("老用户回来了:{},{}",userId,ip);
		}
		return userId;
    }
 
    @PostMapping("/log")
    @Operation(summary = "落地页日志采集",
            responses = {
                    @ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Boolean.class)))
            })
    public Result<Void> log(@RequestBody  LandingPageH5Log obj) {
		try {
			String biz  = obj.getName();
			if(biz == null || "".equals(biz)) {
				biz = "DEFAULT";
				obj.setName(biz);
			}
			String ip = IpUtil.getRequestIp(request); 
			String agent = request.getHeader("user-agent");
			obj.setIp(ip);
			// 优先使用前端传入的 uuid（跨域场景下 Cookie 不可靠），未传时走服务端 Cookie 逻辑
			if (obj.getUuid() == null || obj.getUuid().isEmpty()) {
				obj.setUuid(this.getUserId(ip));
			}
			obj.setUa(agent);
			final String landingPageType  = biz;
			ThreadUtil.execAsync(()->{
				//保存访问日志...
				landingPageLogDao.insertDocument(obj); 
				//异步处理钉钉通知。 每100条访问量钉钉一下..
				String day  = DateUtil.today();
				Map<String,Integer> bizDailyCount = map.get(day);
				if(bizDailyCount  == null) {
					bizDailyCount = new ConcurrentHashMap<>();
					bizDailyCount.put(landingPageType, 0);
					map.put(day, bizDailyCount);
				}
				Integer c  = bizDailyCount.get(landingPageType)  ;
				if(c == null) {
					c = 0;
				}
				bizDailyCount.put(landingPageType, ++c); //计数器加1；
				if(c % 100  == 0) {
					//每加到100个，发一次钉钉通知...
					sendDDNotify(landingPageType,c,day);
					//清除昨天数据，防止内存泄漏...
				}
			});
			return Result.success();
		}catch(Exception ex) {
        	log.error(ex.getMessage()); 
        	return Result.failed();
        }
       
    }
    
    
    @PostMapping("/logTaobao20260611")
    @Operation(summary = "淘宝活动页临时日志采集",
            responses = {
                    @ApiResponse(description = "成功信息", content = @Content(schema = @Schema(implementation = Boolean.class)))
            })
    public Result<Void> logTaobao20260611(@RequestBody  LandingPageH5Log obj) {
		try {
			String biz  = obj.getName();
			if(biz == null || "".equals(biz)) {
				biz = "Taobao20260611";
				obj.setName(biz);
			}
			String ip = IpUtil.getRequestIp(request); 
			String agent = request.getHeader("user-agent");
			obj.setIp(ip);
			// 优先使用前端传入的 uuid（跨域场景下 Cookie 不可靠），未传时走服务端 Cookie 逻辑
			if (obj.getUuid() == null || obj.getUuid().isEmpty()) {
				obj.setUuid(this.getUserId(ip));
			}
			obj.setUa(agent);
			ThreadUtil.execAsync(()->{
				//保存访问日志...
				landingPageLogDao.insertDocument(obj); 
			});
			return Result.success();
		}catch(Exception ex) {
        	log.error(ex.getMessage()); 
        	return Result.failed();
        }
       
    }
 
    
    /**
     * 碰一碰落地页访问报告
     * @param landingPageType
     * @param c
     * @param today
     */
    private  void sendDDNotify(String landingPageType,int c,String today) {
   	 // Markdown消息标题
       String title = "碰一碰落地页访问报告";
       
       // 正确的Markdown格式示例
       String text = "### 🚀 "+title+"\n\n" +
                    "**📋 落地页名称**: "+landingPageType+"\n\n" +
                    "**📊 当前访问次数**: ✅ "+c+"\n\n" +
                    "---\n\n" ;
       
       
       StringBuffer todayData  = new StringBuffer("**📈 今日数据统计**:\n\n");
       Map<String, Integer> bizDailyCount = map.get(today);
       for(String biz: bizDailyCount.keySet()) {
    	   todayData.append("📝").append(biz).append("\t\t").append(bizDailyCount.get(biz)).append("笔\n");
       }
       todayData.append(  "---\n\n");

       StringBuffer yestodayData  = new StringBuffer("**📈 昨日数据统计**:\n\n");
       String yestoday  = DateUtil.formatDate(DateUtil.yesterday()) ;
       Map<String, Integer> bizDailyCount2 = map.get(yestoday);
       for(String biz: bizDailyCount2.keySet()) {
    	   yestodayData.append("📝").append(biz).append("\t\t").append(bizDailyCount2.get(biz)).append("笔\n");
       }
       yestodayData.append(  "---\n\n");
       
       
       String tmp =   text + todayData.toString() + yestodayData.toString() +   
                    "---\n\n" +
                    "**🔔 重要提醒**:\n\n" +
                    "> 📞 如有问题请联系技术支持: @18928909842\n\n" +
                    "---\n\n" +
                    "📅 时间: " + DateUtil.now() + "\n\n" +
                    "👤 报告人: GFW落地页监控机器人";
       
       
       
       DingdingRobot.sendDDMessageMarkDown1(title, tmp);
   }
    
}
