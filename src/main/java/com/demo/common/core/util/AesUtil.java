package com.demo.common.core.util;

import cn.hutool.core.codec.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Key;

public class AesUtil {

	private static final Logger logger  = LoggerFactory.getLogger(AesUtil.class);
	
	/**************************************************************************************/

	private static String key = "8e5915510bbRosun"; // use your own Key here
	private static String iv = "7E4A6B26081ROSUN"; // use your own IV here

	private static Key getKey(String key) throws Exception {
		byte[] keyBytes = key.getBytes("UTF-8");
		SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
		return newKey;
	}

	private static AlgorithmParameters getIV(String iv) throws Exception {
		byte[] ivs = iv.getBytes("UTF-8");
		AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
		params.init(new IvParameterSpec(ivs));
		return params;
	}

	public static String encrypt(String text)   {
		try {
			return encrypt(key,iv,text);
		} catch (Exception e) {
			logger.warn(e.getMessage(),e);
			//e.printStackTrace();
		}
		return null;
	}

	public static String decrypt(String text)  {
		try {
			return decrypt(key,iv,text);
		} catch (Exception e) {
			logger.warn(e.getMessage(),e);
			//e.printStackTrace();
		}
		return null;
	}

	private static String encrypt(String key, String iv, String text) throws Exception {
		// LOGGER.info("$$$AESDemo.encrypt: key= "+key + ", iv= " + iv + ",text=
		// " + text);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, getKey(key), getIV(iv));
		byte[] encryptedBytes = cipher.doFinal(text.getBytes());
		return new String(Base64.encode(encryptedBytes, false, true), "UTF-8");
	}

	private static String decrypt(String key, String iv, String text) throws Exception {
		// LOGGER.info("$$$AESDemo.decrypt: key= "+key + ", iv= " + iv + ",text=
		// " + text);
		byte[] textBytes = Base64.decode(text);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, getKey(key), getIV(iv));
		byte[] decodedBytes = cipher.doFinal(textBytes);
		return new String(decodedBytes, "UTF-8");
	}

}
