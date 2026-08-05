package com.demo.weixin.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
//
//    /**
//     * 配置CORS跨域
//     */
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/api/**")
//                .allowedOriginPatterns("*")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(true)
//                .maxAge(3600);
//    }

	@Bean
    public CorsFilter corsFilter() {
        // 1. 创建CORS配置对象
        CorsConfiguration config = new CorsConfiguration();
        // 设置允许的域，* 代表允许任何域。生产环境建议设置具体域名，如 "https://www.example.com"
        config.addAllowedOriginPattern("*"); // 推荐使用 addAllowedOriginPattern 替代 addAllowedOrigin
        // 是否允许发送Cookie等凭证信息
        config.setAllowCredentials(true);
        // 设置允许的请求方式，* 代表所有（GET, POST, PUT, DELETE等）
        config.addAllowedMethod("*");
        // 设置允许的头部信息，* 代表所有头部
        config.addAllowedHeader("*");

        // 2. 为所有接口配置CORS规则
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // /** 代表所有路径

        // 3. 返回CorsFilter对象
        return new CorsFilter(source);
    }
}
