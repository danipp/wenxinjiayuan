package com.demo.weixin.conf;

import com.demo.weixin.interceptor.AdminLoginInterceptor;
import com.demo.weixin.interceptor.UserLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorsConfig implements WebMvcConfigurer {

    @Bean
    public UserLoginInterceptor userLoginInterceptor() {
        return new UserLoginInterceptor();
    }

    @Bean
    public AdminLoginInterceptor adminLoginInterceptor() {
        return new AdminLoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userLoginInterceptor()).addPathPatterns("/api/**");
        registry.addInterceptor(adminLoginInterceptor()).addPathPatterns("/manage/**");
    }
}
