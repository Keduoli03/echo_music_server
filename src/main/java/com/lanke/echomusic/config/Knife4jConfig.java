package com.lanke.echomusic.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .name("Authorization") // 关键修改：名称改为 "Authorization"
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("请在请求头中携带 JWT 令牌，格式为：Bearer <token>");

        return new OpenAPI()
                .info(new Info().title("EchoMusic API 文档").version("1.0"))
                .components(new Components().addSecuritySchemes("Authorization", securityScheme))
                .addSecurityItem(new SecurityRequirement().addList("Authorization")); // 引用名称为 "Authorization"
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .packagesToScan("com.lanke.echomusic.controller")
                .pathsToMatch("/api/**")
                .build();
    }
}