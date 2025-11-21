package com.mengyo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j (Swagger) 配置
 *
 * @author Mengyo Team
 * @since 2025-11-21
 */
@Configuration
public class Knife4jConfig {

    /**
     * OpenAPI配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("萌友益站API文档")
                        .description("萌友益站 - 公益救助平台后端接口文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("support@mengyo.com")
                                .url("https://mengyo.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }

    /**
     * 用户模块API分组
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("1. 用户模块")
                .pathsToMatch("/user/**", "/auth/**")
                .build();
    }

    /**
     * 救助模块API分组
     */
    @Bean
    public GroupedOpenApi rescueApi() {
        return GroupedOpenApi.builder()
                .group("2. 救助模块")
                .pathsToMatch("/rescue/**")
                .build();
    }

    /**
     * 领养模块API分组
     */
    @Bean
    public GroupedOpenApi adoptionApi() {
        return GroupedOpenApi.builder()
                .group("3. 领养模块")
                .pathsToMatch("/adoption/**")
                .build();
    }

    /**
     * 捐赠模块API分组
     */
    @Bean
    public GroupedOpenApi donationApi() {
        return GroupedOpenApi.builder()
                .group("4. 捐赠模块")
                .pathsToMatch("/donation/**")
                .build();
    }

    /**
     * 社区模块API分组
     */
    @Bean
    public GroupedOpenApi communityApi() {
        return GroupedOpenApi.builder()
                .group("5. 社区模块")
                .pathsToMatch("/community/**")
                .build();
    }

    /**
     * 系统模块API分组
     */
    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder()
                .group("6. 系统模块")
                .pathsToMatch("/system/**", "/notification/**")
                .build();
    }

}
