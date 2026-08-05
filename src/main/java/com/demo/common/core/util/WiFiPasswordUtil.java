package com.demo.common.core.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.hutool.core.date.DateUtil;

/**
 * 确定性映射算法：
 * 用 SimpleDateFormat("yyyyMMdd") 提取日期部分，如 20260602
 * 通过 Calendar.HOUR_OF_DAY 判断 <12 为 AM，否则为 PM
 * 拼接种子字符串，如 "20260602AM" → 同一天同一时段必然相同
 * 对种子取 hashCode()，用 & 0xFFFFFFFFL 转无符号避免负数，再 % 90000000 + 10000000 映射到 8 位正整数（10000000 ~ 99999999）
 * 特性：

 * 同一天上午/下午各一个确定的 8 位密码
 * 不同日期必然不同，同一时段内完全一致
 * 异常时返回 88888888 兜底
 */
public class WiFiPasswordUtil {

	/**
	 * 规则： 根据参数date时间，例如是在某天的上午或者下午， 获取年月日+AM或者PM(分别表示上午和下午) 的格式化的字符串，然后使用确定性的某个有规律的算法，映射生成一个8位的正整数；这个8位的正整数是跟date时间相关的，且每一天的上午和下午都确定性对应一个正整数。
	 * @return
	 */
	public static Integer generateFromDateTime(Date date) {
		try {
			// 格式化日期为 yyyyMMdd
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String dateStr = sdf.format(date);

			// 判断上午(AM)或下午(PM)：12点前为AM，12点及之后为PM
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			String period = cal.get(Calendar.HOUR_OF_DAY) < 12 ? "AM" : "PM";

			// 用日期+时段拼接成种子字符串，保证同一天同一时段确定性一致
			String seed = dateStr + period;

			// 对种子做哈希，取无符号值，映射到 10000000~99999999 的8位正整数
			int hash = seed.hashCode();
			long unsignedHash = hash & 0xFFFFFFFFL;
			return (int) (unsignedHash % 90000000L) + 10000000;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 88888888; //返回兜底的异常处理兼容的正整数。
	}
	
	
	
	public static void main(String args[]) {
		System.out.println(generateFromDateTime(new Date()));
		System.out.println(generateFromDateTime(new Date()));
//		System.out.println(generateFromDateTime(DateUtil.offsetDay(new Date(), 1)));
	}
}
