package com.demo.common.core.util;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

;

/**
 * 公钥和私钥是成对的，公钥加密要用私钥解密，私钥加密要用公钥解密。每次加密结果是变化的。
 * 1、公钥加密，私钥解密用于信息加密。
 * 2、私钥加密，公钥解密。
 * 3、私钥签名，公钥验证。在https中公钥任何人都有，用私钥加密意义不大，还不如使用签名。
 * 签名实际上是对明文的摘要做了加密。收到的人会收到明文和签名，利用公钥可以进行验证真伪，数字签名保证信息是服务端发的，并且没有遭到篡改。
 * 前面两点是加密，后面一点是认证。两者的侧重点是不同的。后面一点鉴别服务端的真伪
 * @2023年9月12日
 * https://blog.csdn.net/u014644574/article/details/128810787
 */
public class RSAUtil {
    //定义加密方式
    private static final String KEY_RSA = "RSA";
    //定义签名算法
    private final static String KEY_RSA_SIGNATURE = "MD5withRSA";
    //公钥
    public final static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCvusFFU4Yiwi/8AY9bIgC38dVRMdffbi70yxz0SpuKlm6+sJ8qia8kt7B2IQ1BQmfrTM3XOmbdtOZFmCvTGAXDkrWrFCDrcAgGNjvIdIppZp99Tq8I6z8VbyBbOlwt53nEWA1Z5AS4yIbDTTYPlQsM2xkuhF4tjbvLABssvEk62QIDAQAB";
    //私钥
    private final static String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAK+6wUVThiLCL/wBj1siALfx1VEx199uLvTLHPRKm4qWbr6wnyqJryS3sHYhDUFCZ+tMzdc6Zt205kWYK9MYBcOStasUIOtwCAY2O8h0imlmn31OrwjrPxVvIFs6XC3necRYDVnkBLjIhsNNNg+VCwzbGS6EXi2Nu8sAGyy8STrZAgMBAAECgYACCaAetPttpdwSdy762kwGdy3aO5dCJdcWBo5M5EJGvpsFINYnVYqDl+dfGyInFSx2OYAAG5o7of1UI6l4pgtrMalZYvT7fnBGZnBF6/CArH/LcmmwpQsxeQqiI6l04kWQBkEA82cappLhsS3vk8uPUQn+pKiTtKLfWZGEsMnf8wJBANA1t5TrmdrXhsNO2fg5ld6389dqmmOIJRFDFsgyJQbQkXsdBKPKTumyFcD9MgKMEJbX7uPrBHikscpmBJS3lGsCQQDYEIOUa3+hodUOvoUeibpFL+3JggysltrG5g8ZqAna4IjvpSIDhuyW/Sn/z6C33NHUZ5XpKtnq+n44RCUmdh7LAkEAz5I+C0E2iW4E9AFQXJ9MQlWxyziD4maATc1MkRkswLZ90vcDASdXtLPN7DCAAvoQL9HmX2KVepD2jolWV80NMQJAFQ225JV9AFD2o9MowYsGHYvIDeSgO4kPlGYCfquDGlLaxQ9AOzvAfg35M7YVoRThVWMqlmfYiC8UcYMiY5cK9QJAQNlgyTYaBpNI5UYOpm6gmbQt8eFfGGZNQyOWI6tInot1jO64K8DCbEkHQTuK8rZtU06FiaE2dsKYqlViZds7ew==";


    public static String decrypt( String enStr ){
        if(StringUtils.isEmpty(enStr)) return null;
        try {
            return RSAUtil.decryptByPrivate(enStr, privateKey);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static String encrypt( String enStr ){
        if(StringUtils.isEmpty(enStr)) return null;
        try {
            return RSAUtil.encryptByPublic(enStr, publicKey);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String encryptByPrivate( String enStr ){
        if(StringUtils.isEmpty(enStr)){
            return null;
        }
        try {
            return RSAUtil.encryptByPrivate(enStr, privateKey);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String sign( String enStr ){
        if(StringUtils.isEmpty(enStr)){
            return null;
        }
        try {
            return RSAUtil.sign(enStr, privateKey);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


/*
    public static void main(String[] args) {
        //生成新的公私密钥对
//        init();
        String str = "cvdsgfedt5346745";
        String enStr1 = RSAUtil.encryptByPublic(str, publicKey);
        System.out.println("公钥加密后：" + enStr1);
        String deStr1 = RSAUtil.decryptByPrivate("VwmgQEiRahpxZIf3KnTFFRIsrkDN8aYFeOu+88ES6qJX35lGhdaZ00pMyZKEQDSxxcZXS4RAnTAjYKUgseLrXJpToE6qwwbcjQikT7i/J6xz2OzYp5XJv3LE/DemZheTX9NUSY9fCFlccFSTAwFHxYNwM9+M5lokDBupGuvDNqw=", privateKey);
        System.out.println("私钥解密后：" + deStr1);
    }*/

    /**
     * 生成公私密钥对
     * @date:2018年10月31日 上午11:16:41
     */
    private static void init() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_RSA);
            //设置密钥对的bit数，越大越安全，但速度减慢，一般使用512或1024
            generator.initialize(1024);
            KeyPair keyPair = generator.generateKeyPair();
            // 获取公钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            // 获取私钥
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            // Base64编码的公钥字符串
            String publicKeyStr = encryptBase64(publicKey.getEncoded());
            //Base64编码的私钥字符串
            String privateKeyStr = encryptBase64(privateKey.getEncoded());
            // 打印密钥对
            System.out.println("publicKey=" + publicKeyStr);
            System.out.println("privateKey=" + privateKeyStr);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * BASE64 解码
     * @param key 需要Base64解码的字符串
     * @date:2018年10月31日 上午11:17:46
     */
    private static byte[] decryptBase64(String key) {
        return Base64.getDecoder().decode(key);
    }

    /**
     * BASE64 编码
     * @param key 需要Base64编码的字节数组
     * @date:2018年10月31日 上午11:18:03
     */
    private static String encryptBase64(byte[] key) {
        return new String(Base64.getEncoder().encode(key));
    }

    /**
     * 公钥加密
     * @param encryptingStr 字符串
     * @param publicKeyStr  公钥
     * @date:2018年10月31日 上午11:18:17
     */
    private static String encryptByPublic(String encryptingStr, String publicKeyStr) {
        try {
            // 将公钥由字符串转为UTF-8格式的字节数组
            byte[] publicKeyBytes = decryptBase64(publicKeyStr);
            // 获得公钥
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            // 取得待加密数据
            byte[] data = encryptingStr.getBytes(StandardCharsets.UTF_8);
            KeyFactory factory = KeyFactory.getInstance(KEY_RSA);
            PublicKey publicKey = factory.generatePublic(keySpec);
            // 对数据加密
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            // 返回加密后由Base64编码的加密信息
            return encryptBase64(cipher.doFinal(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 私钥解密
     * @param encryptedStr  字符串
     * @param privateKeyStr 私钥
     * @date:2018年10月31日 上午11:18:36
     */
    private static String decryptByPrivate(String encryptedStr, String privateKeyStr) {
        try {
            // 对私钥解密
            byte[] privateKeyBytes = decryptBase64(privateKeyStr);
            // 获得私钥
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            // 获得待解密数据
            byte[] data = decryptBase64(encryptedStr);
            KeyFactory factory = KeyFactory.getInstance(KEY_RSA);
            PrivateKey privateKey = factory.generatePrivate(keySpec);
            // 对数据解密
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            // 返回UTF-8编码的解密信息
            return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 私钥加密
     * @param encryptingStr 字符串
     * @param privateKeyStr 私钥
     * @date:2018年10月31日 上午11:18:57
     */
    private static String encryptByPrivate(String encryptingStr, String privateKeyStr) {
        try {
            byte[] privateKeyBytes = decryptBase64(privateKeyStr);
            // 获得私钥
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            // 取得待加密数据
            byte[] data = encryptingStr.getBytes(StandardCharsets.UTF_8);
            KeyFactory factory = KeyFactory.getInstance(KEY_RSA);
            PrivateKey privateKey = factory.generatePrivate(keySpec);
            // 对数据加密
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            // 返回加密后由Base64编码的加密信息
            return encryptBase64(cipher.doFinal(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 公钥解密
     * @param encryptedStr 字符串
     * @param publicKeyStr 公钥
     * @date:2018年10月31日 上午11:19:07
     */
    private static String decryptByPublic(String encryptedStr, String publicKeyStr) {
        try {
            // 对公钥解密
            byte[] publicKeyBytes = decryptBase64(publicKeyStr);
            // 取得公钥
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            // 取得待加密数据
            byte[] data = decryptBase64(encryptedStr);
            KeyFactory factory = KeyFactory.getInstance(KEY_RSA);
            PublicKey publicKey = factory.generatePublic(keySpec);
            // 对数据解密
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            // 返回UTF-8编码的解密信息
            return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用私钥对私钥加密数据进行签名
     * @param encryptedStr 私钥加密后的字符串
     * @param privateKey   私钥
     * @date:2018年10月31日 上午11:19:37
     */
    private static String sign(String encryptedStr, String privateKey) {
        String str = "";
        try {
            //将私钥加密数据字符串转换为字节数组
            byte[] data = encryptedStr.getBytes(StandardCharsets.UTF_8);
            // 解密由base64编码的私钥
            byte[] bytes = decryptBase64(privateKey);
            // 构造PKCS8EncodedKeySpec对象
            PKCS8EncodedKeySpec pkcs = new PKCS8EncodedKeySpec(bytes);
            // 指定的加密算法
            KeyFactory factory = KeyFactory.getInstance(KEY_RSA);
            // 取私钥对象
            PrivateKey key = factory.generatePrivate(pkcs);
            // 用私钥对信息生成数字签名
            Signature signature = Signature.getInstance(KEY_RSA_SIGNATURE);
            signature.initSign(key);
            signature.update(data);
            str = encryptBase64(signature.sign());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 校验数字签名
     * @param encryptedStr 私钥加密后的字符串
     * @param publicKey    公钥
     * @param sign         签名
     * @date:2018年10月31日 上午11:19:50
     */
    private static boolean verify(String encryptedStr, String publicKey, String sign) {
        try {
            //将私钥加密数据字符串转换为字节数组
            byte[] data = encryptedStr.getBytes(StandardCharsets.UTF_8);
            // 解密由base64编码的公钥
            byte[] bytes = decryptBase64(publicKey);
            // 构造X509EncodedKeySpec对象
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            // 指定的加密算法
            KeyFactory factory = KeyFactory.getInstance(KEY_RSA);
            // 取公钥对象
            PublicKey key = factory.generatePublic(keySpec);
            // 用公钥验证数字签名
            Signature signature = Signature.getInstance(KEY_RSA_SIGNATURE);
            signature.initVerify(key);
            signature.update(data);
            return signature.verify(decryptBase64(sign));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
