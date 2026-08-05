package com.demo.weixin.service;

import cn.hutool.json.JSONUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.demo.weixin.constant.Constants;
import com.demo.weixin.dao.AdminDao;
import com.demo.weixin.entity.Admin;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 管理员 服务实现类
 * </p>
 *
 * @author Zane
 */
@Service
public class AdminService {

	@Autowired
	private AdminDao adminDao;


	@Autowired
	private RedisTemplate<String, String> stringRedisTemplate;

	public void setAdminSession(String token, Admin admin) {
		stringRedisTemplate.opsForValue().set(Constants.REDIS_SESSION_ADMIN + ":" + token, JSONUtil.toJsonStr(admin), 	Constants.SESSION_EXPIRE_TIME, TimeUnit.SECONDS);
		stringRedisTemplate.opsForValue().set(Constants.REDIS_SESSION_ADMIN + ":" + admin.getAdminId(), token, Constants.SESSION_EXPIRE_TIME, TimeUnit.SECONDS);
	}

	public Admin getAdminById(Long adminId) {
		return adminDao.findById(adminId);
	}

	public Admin updateDocument(Map<String, Object> updateMap) {
		return adminDao.updateDocument(updateMap);
	}

	/**
	 * 插入一个admin用户
	 * @param admin
	 * @return
	 */
	public Admin createtAdmini(Admin admin) {
		return  adminDao.insertDocument(admin);
	}

	public Admin findOne(Criteria criteria) {
		return adminDao.findOne(criteria);
	}
	public Admin aaa(Criteria criteria) {


		return adminDao.findOne(criteria);
	}

}
