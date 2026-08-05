package com.demo;

import com.demo.common.core.util.SpringBeanContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@Slf4j
public class WarmHomeApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarmHomeApplication.class, args);

        System.out.println
        	   ("(♥◠‿◠)ﾉﾞ温馨家园项目 启动成功   ლ(´ڡ`ლ)ﾞ  \n"+
				"       _    \n" +
				"      | |   \n" +
				"  ___ | | __\n" +
				" / _ \\| |/ /\n" +
				"| (_) |   < \n" +
				" \\___/|_|\\_\\");
        System.out.println("当前系统环境:" + SpringBeanContextHolder.getEnv());
    }
       

}
