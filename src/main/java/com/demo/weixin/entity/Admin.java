package com.demo.weixin.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员 使用手机号和密码登录；
 */
@Data
@NoArgsConstructor
@Document(collection = "admin")
@Schema(description = "管理员")
public class Admin extends Base {

	@Field
	private Long adminId; // 对于业务实体的实际id

	/**
	 * 昵称（登录后显示），默认 是用户+手机尾号4位
	 */
	@Field
	@Schema(description = "昵称")
	private String nickName;

	/**
	 * 登录用的密码
	 */
	@Field
	@Schema(description = "密码，使用rsa加密传给后台")
	private String passWord;

	/**
	 * 手机
	 */
	@Field
	@Schema(description = "手机")
	private String cellphone;

	/**
	 * 头像
	 */
	@Field
	@Schema(description = "头像url")
	private String avatar;

	/**
	 * 描述
	 */
	@Field
	private String description;

	/**
	 * 微信OpenId
	 */
	@Field
	private String openId;

	@Override
	public Long getID() {
		return adminId;
	}

	@Override
	public void setID(Long id) {
		this.adminId = id;
	}
}
