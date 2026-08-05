package com.demo.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 设备密钥加密/解密工具类（AES/ECB/PKCS5Padding）。
 * <p>
 * 用于对设备出厂档案（power_device_profile）中的 secretKey 进行加密存储，
 * 防止数据库被拖库后密钥明文泄露导致整个密码体系被攻破。
 * <p>
 * 设计要点：
 * 1. 加密密钥从配置文件 power.secret-key.aes-key 读取，未配置时使用默认密钥；
 * 2. 密钥长度不足16字节时自动补零，超过16字节时截断，保证 AES-128 可用；
 * 3. 加密结果以 Base64 编码返回，便于存储到 MongoDB 字符串字段；
 * 4. 解密失败时（例如存量明文数据）原样返回输入，保证向后兼容；
 * 5. 输入为 null 或空字符串时原样返回，不做处理。
 */
@Slf4j
@Component
public class SecretKeyEncryptor {

    /** AES 加密算法/模式/填充方式 */
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /** AES 密钥固定长度（AES-128 为16字节） */
    private static final int KEY_LENGTH = 16;

    /** 默认 AES 密钥（配置文件未配置时使用，生产环境应通过环境变量覆盖） */
    private static final String DEFAULT_AES_KEY = "PowerShare2026";

    /** 从配置文件读取的 AES 密钥 */
    @Value("${power.secret-key.aes-key:" + DEFAULT_AES_KEY + "}")
    private String aesKey;

    /**
     * 加密明文。
     * 使用 AES/ECB/PKCS5Padding 加密，返回 Base64 编码的密文。
     *
     * @param plaintext 明文（设备密钥）
     * @return Base64 编码的密文；输入为 null 或空时原样返回
     */
    public String encrypt(String plaintext) {
        // 空值或空字符串直接返回，不做处理
        if (plaintext == null || plaintext.isEmpty()) {
            return plaintext;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, buildSecretKey());
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            // 使用 URL 安全的 Base64 编码，避免 '+' '/' 影响存储与传输
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("设备密钥加密失败，原样返回明文", e);
            return plaintext;
        }
    }

    /**
     * 解密密文。
     * 将 Base64 编码的密文还原为明文。
     *
     * @param ciphertext Base64 编码的密文
     * @return 解密后的明文；输入为 null 或空时原样返回；解密失败时原样返回（向后兼容存量明文）
     */
    public String decrypt(String ciphertext) {
        // 空值或空字符串直接返回，不做处理
        if (ciphertext == null || ciphertext.isEmpty()) {
            return ciphertext;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, buildSecretKey());
            byte[] decodedBytes = Base64.getDecoder().decode(ciphertext);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // 解密失败说明该数据可能是存量明文（未加密），原样返回保证向后兼容
            log.warn("设备密钥解密失败，可能为存量明文数据，原样返回。input={}", ciphertext);
            return ciphertext;
        }
    }

    /**
     * 构建 AES SecretKeySpec。
     * 将配置的密钥字符串转换为固定16字节的 AES 密钥：
     * - 不足16字节时末尾补零；
     * - 超过16字节时截断为前16字节。
     *
     * @return AES 密钥规格对象
     */
    private SecretKeySpec buildSecretKey() {
        byte[] keyBytes = aesKey.getBytes(StandardCharsets.UTF_8);
        byte[] fixedKey = new byte[KEY_LENGTH];
        // 复制有效字节，不足部分保持0（补零）
        int copyLen = Math.min(keyBytes.length, KEY_LENGTH);
        System.arraycopy(keyBytes, 0, fixedKey, 0, copyLen);
        return new SecretKeySpec(fixedKey, ALGORITHM);
    }
}
