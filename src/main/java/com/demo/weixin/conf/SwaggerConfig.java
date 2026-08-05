package com.demo.weixin.conf;

import com.demo.weixin.constant.Constants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(Constants.TOKEN, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)  // 使用 APIKEY 类型
                                .in(SecurityScheme.In.HEADER)
                                .name(Constants.TOKEN)  // 请求头的键名
                                .description("校验用户使用"))
                        .addSecuritySchemes(Constants.TOKEN_ADMIN, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)  // 使用 APIKEY 类型
                                .in(SecurityScheme.In.HEADER)
                                .name(Constants.TOKEN_ADMIN)  // 请求头的键名
                                .description("校验后台用户使用")))
                .addSecurityItem(new SecurityRequirement().addList(Constants.TOKEN))
                .addSecurityItem(new SecurityRequirement().addList(Constants.TOKEN_ADMIN))
                .info(this.getApiInfo());
    }

    private Info getApiInfo() {
        return new Info()
                // 配置文档标题
                .title("发券中心")
                // 配置文档描述
                .description("发券中心文档")
                // 配置版本号
                .version("1.0.0");
    }
}
