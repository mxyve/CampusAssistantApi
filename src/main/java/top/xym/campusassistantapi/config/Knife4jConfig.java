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

    @Bean
    public GroupedOpenApi userApi() {
        String[] paths = {"/**"};
        String[] packagedToMatch = {"top.xym.campusassistantapi.module.user"};
        return GroupedOpenApi.builder()
                .group("2")
                .displayName("User API")
                .pathsToMatch(paths)
                .packagesToScan(packagedToMatch).build();
    }

    @Bean
    public GroupedOpenApi chatApi() {
        String[] paths = {"/**"};
        String[] packagedToMatch = {"top.xym.campusassistantapi.module.chat"};
        return GroupedOpenApi.builder()
                .group("3")
                .displayName("Chat API")
                .pathsToMatch(paths)
                .packagesToScan(packagedToMatch).build();
    }

    @Bean
    public GroupedOpenApi sessionApi() {
        String[] paths = {"/**"};
        String[] packagedToMatch = {"top.xym.campusassistantapi.module.session"};
        return GroupedOpenApi.builder()
                .group("4")
                .displayName("Session API")
                .pathsToMatch(paths)
                .packagesToScan(packagedToMatch).build();
    }

    @Bean
    public GroupedOpenApi messageApi() {
        String[] paths = {"/**"};
        String[] packagedToMatch = {"top.xym.campusassistantapi.module.message"};
        return GroupedOpenApi.builder()
                .group("5")
                .displayName("Message API")
                .pathsToMatch(paths)
                .packagesToScan(packagedToMatch).build();
    }

    @Bean
    public GroupedOpenApi studyApi() {
        String[] paths = {"/**"};
        String[] packagedToMatch = {"top.xym.campusassistantapi.module.study"};
        return GroupedOpenApi.builder()
                .group("6")
                .displayName("Study API")
                .pathsToMatch(paths)
                .packagesToScan(packagedToMatch).build();
    }

    @Bean
    public GroupedOpenApi agentApi() {
        String[] paths = {"/**"};
        String[] packagedToMatch = {"top.xym.campusassistantapi.module.agent"};
        return GroupedOpenApi.builder()
                .group("7")
                .displayName("Agent API")
                .pathsToMatch(paths)
                .packagesToScan(packagedToMatch).build();
    }

}
