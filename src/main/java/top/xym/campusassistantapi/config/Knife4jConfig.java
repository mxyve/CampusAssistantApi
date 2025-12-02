package top.xym.campusassistantapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("智能校园助手API 接口文档")
                        .version("1.0.0")
                        .summary("智能校园助手API 接口文档")
                        .description("智能校园助手API 演示项目")
                        .contact(new Contact()
                                .name("xym")
                                .email("1286280961@qq.com")
                        ));
    }

    @Bean
    public GroupedOpenApi authApi() {
        String[] paths = {"/**"};
        String[] packagedToMatch = {"top.xym.campusassistantapi.module.auth"};
        return GroupedOpenApi.builder()
                .group("1")
                .displayName("Auth API")
                .pathsToMatch(paths)
                .packagesToScan(packagedToMatch).build();
    }

}
