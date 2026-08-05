package com.demo.weixin.ws;

import java.util.Date;
import java.math.BigDecimal;

//import com.demo.weixin.entity.wifi.WifiAdWatchProfitRecord;
//import com.demo.weixin.entity.wifi.WifiDeveloper;
//import com.demo.weixin.entity.wifi.WifiOwner;
//import com.demo.weixin.entity.wifi.WifiPoint;
//import com.demo.weixin.vo.wifi.MoneyDingdingItemVO;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class WebSocketHandler extends BaseWebSocketHandler {
	
	
	private static final WebSocketHandler WS_HANDLER =  new WebSocketHandler();
	
	public static WebSocketHandler getWebSocketHandler() {
		return WS_HANDLER;
	}
	
	
	@Override
	void doRegistEventProcessor() {
		super.registEventProcessor("QUERY", new WebsocketEventProcesser() {
			@Override
			public void onEvent(WebSocketEventMsg event, WsUser wsUser) {
				long bizId = wsUser.getPartnerId();
				pushUpdate(bizId);
			}
		});
	}
	
	
	public void pushUpdate(Long prjId) {
		if(prjId == null) {
			log.error("prjId is null~~~");
			return;
		}
//		GlobalDataVO globalData = SpringBeanContextHolder.getBean(NfcService.class).getProjectDashboardData(prjId);
//		WebSocketEventMsg respEvent = new WebSocketEventMsg("UPDATE",globalData);
//		try {
//			String bizId = prjId.toString();
//			WS_HANDLER.sendMessage(bizId, respEvent);
//		} catch (IOException e) {
//			log.error(e.getMessage(),e);
//		}
	}


//
//
//	public void pushMoneyDingDing(int dayCount, WifiAdWatchProfitRecord record, WifiOwner owner, WifiDeveloper developer,
//			WifiPoint wifiPoint, Date now) {
//		MoneyDingdingItemVO vo = new MoneyDingdingItemVO();
//		vo.setDateTime(now);
//		vo.setDeveloperId(developer.getDeveloperId());
//		vo.setDeveloperName(developer.getName());
//		vo.setOwnerId(owner.getOwnerId());
//		vo.setPriceDeveloper(record.getPriceDeveloper());
//		vo.setPriceAgent(record.getPriceAgent());
//		vo.setPriceOwner(record.getPriceOwner());
//		vo.setRepeat(dayCount != 1);// 是否重复
//		vo.setSiteName(wifiPoint.getSiteName());
//		vo.setWifiName(wifiPoint.getWifiName());
//		vo.setWifiOwnerName(wifiPoint.getOwnerName());
//		WebSocketEventMsg respEvent = new WebSocketEventMsg("MONEY-DING", vo);
//		// 推店主
//		try {
//			WS_HANDLER.sendMessage(owner.getOwnerId(), respEvent);
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//		}
//
//		// 推开发者
//		try {
//			WS_HANDLER.sendMessage(developer.getDeveloperId(), respEvent);
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//		}
//
//		// 推代理
//		try {
//			WS_HANDLER.sendMessage(developer.getAgentId(), respEvent);
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//		}
//
//	}
	
	private static  int c  = 0;

}