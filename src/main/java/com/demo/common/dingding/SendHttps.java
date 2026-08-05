package com.demo.common.dingding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import cn.hutool.json.JSONUtil;
 
/**
 * @描述 发送http请求的工具类
 * @创建人 caoju
 * @创建时间 2022/1/15 8:56
 */
public class SendHttps {
 
//    private static Logger logger = LoggerFactory.getLogger(SendHttps.class);
 
    /**
     * 发送POST请求，参数是Map, contentType=x-www-form-urlencoded
     *
     * @param url
     * @param mapParam
     * @return
     */
    public static String sendPostByMap(String url, Map<String, Object> mapParam) {
    	try {
    		 Map<String, String> headParam = new HashMap<>();
    	        headParam.put("Content-type", "application/json;charset=UTF-8");
    	     return sendPost(url, mapParam, headParam);
    	}catch(Exception ex) {
//    		log.error(ex.getMessage(),ex);
    		ex.printStackTrace();
    		return "ERROR:" + ex.getMessage();
    	}
       
    }
 
    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, Map<String, Object> param, Map<String, String> headParam) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性 请求头
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Fiddler");
 
            if (headParam != null) {
                for (Entry<String, String> entry : headParam.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
//            out.print(JSON.toJSONString(param));
            out.print(JSONUtil.toJsonStr(param));
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * 构建Markdown消息的JSON字符串
     */
    public static String buildMarkdownJson(String title, String text, 
                                          List<String> atMobiles, boolean isAtAll) {
        // 使用字符串拼接构建JSON，避免额外依赖
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("\"msgtype\": \"markdown\",");
        jsonBuilder.append("\"markdown\": {");
        jsonBuilder.append("\"title\": \"").append(escapeJsonString(title)).append("\",");
        jsonBuilder.append("\"text\": \"").append(escapeJsonString(text)).append("\"");
        jsonBuilder.append("},");
        jsonBuilder.append("\"at\": {");
        jsonBuilder.append("\"atMobiles\": [");
        
        if (atMobiles != null && !atMobiles.isEmpty()) {
            for (int i = 0; i < atMobiles.size(); i++) {
                if (i > 0) jsonBuilder.append(",");
                jsonBuilder.append("\"").append(atMobiles.get(i)).append("\"");
            }
        }
        
        jsonBuilder.append("],");
        jsonBuilder.append("\"isAtAll\": ").append(isAtAll);
        jsonBuilder.append("}");
        jsonBuilder.append("}");
        
        return jsonBuilder.toString();
    }
    
    /**
     * 转义JSON字符串中的特殊字符
     */
    private static String escapeJsonString(String value) {
    	 return value.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\n", "\\n")  // 保留换行符转义
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }
    
    /**
     * 执行HTTP POST请求
     */
    public static boolean doPostRequest(String url, String jsonBody) {
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
            
            StringEntity entity = new StringEntity(jsonBody, "UTF-8");
            httpPost.setEntity(entity);
            
            HttpResponse response = httpClient.execute(httpPost);
            System.out.println("response.getStatusLine().getStatusCode()="+response.getStatusLine().getStatusCode());
            String responseStr = EntityUtils.toString(response.getEntity());
            System.out.println("responseStr="+responseStr);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 简单判断是否成功，成功时errcode为0
                return responseStr.contains("\"errcode\":0");
            }
        } catch (Exception e) {
            System.err.println("HTTP请求异常: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
