package com.demo.common.core.util;

import java.util.Arrays;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring Bean 上下文持有器
 * 用于在非Spring管理类中获取Spring容器中的Bean
 */
@Component
@Slf4j
public class SpringBeanContextHolder implements ApplicationContextAware {

 
	
	
	private static final String ENV_PROD = "prod";
	
	private static final String ENV_TEST  = "test";
	
	private static final String ENV_DEVP = "devp";
	
	
	private static String env = null;
	
	
	public static String getEnv() {
		initializeEnv();
		return env;
	}
	
	public static boolean isTestEnv() {
		if(applicationContext == null) {
			return true;
		}
		initializeEnv();
		return ENV_TEST.equals(env);
	}
	
	public static boolean isDevpEnv() {
		if(applicationContext == null) {
			return true;
		}
		initializeEnv();
		return ENV_DEVP.equals(env);
	}
	
	public static boolean isProdEnv() {
		if(applicationContext == null) {
			return true;
		}
		initializeEnv();
		return ENV_PROD.equals(env);
	}
	
	
	private static void initializeEnv() {
		if(env == null) {
			try {
				 Environment environment = applicationContext.getEnvironment();
//				 log.info("environment:{}",JSONUtil.toJsonStr(environment));
				 if (Arrays.asList(environment.getActiveProfiles()).contains(ENV_PROD)) {
				        env = ENV_PROD;
				 }
				 if (Arrays.asList(environment.getActiveProfiles()).contains(ENV_TEST)) {
				        env = ENV_TEST;
				 }
				 if (Arrays.asList(environment.getActiveProfiles()).contains(ENV_DEVP)) {
				        env = ENV_DEVP;
				 }
			}catch(Exception ex) {
				log.error(ex.getMessage(),ex);
			}
			
		}
	}


	private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanContextHolder.applicationContext = applicationContext;
    }

    /**
     * 获取ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return applicationContext;
    }

    /**
     * 根据名称获取Bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        checkApplicationContext();
        return (T) applicationContext.getBean(name);
    }

    /**
     * 根据类型获取Bean
     */
    public static <T> T getBean(Class<T> clazz) {
        checkApplicationContext();
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据名称和类型获取Bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        checkApplicationContext();
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 检查ApplicationContext是否注入
     */
    private static void checkApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext未注入，请在Spring配置中定义SpringBeanContextHolder");
        }
    }

    /**
     * 清除ApplicationContext
     */
    public static void cleanApplicationContext() {
        applicationContext = null;
    }
}