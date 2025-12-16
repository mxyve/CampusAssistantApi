package top.xym.campusassistantapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import top.xym.starter.common.annotation.EnableMqxuCommon;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableMqxuCommon
@MapperScan({"top.xym.campusassistantapi.module.agent.mapper", "top.xym.campusassistantapi.module.user.mapper", "top.xym.campusassistantapi.module.message.mapper","top.xym.campusassistantapi.module.session.mapper"})
public class CampusAssistantApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusAssistantApiApplication.class, args);
        System.out.println("Campus Assistant API 启动成功!");
        System.out.println("Knife4j 接口文档地址: http://localhost:6060/doc.html");
    }

//    public static void main(String[] args) {
//        SpringApplication.run(CampusAssistantApiApplication.class, args);
//    }
//
//    @Bean
//    public ApplicationListener<ApplicationReadyEvent> applicationReadyEventListener(Environment environment) {
//        return event -> {
//            String port = environment.getProperty("server.port", "6060");
//            String contextPath = environment.getProperty("server.servlet.context-path", "");
//            String accessUrl = "http://localhost:" + port + contextPath + "/chatui/index.html";
//            System.out.println("\n🎉========================================🎉");
//            System.out.println("✅ Smart Campus Assistant is ready!");
//            System.out.println("🚀 Chat with your assistant: " + accessUrl);
//            System.out.println("🎉========================================🎉\n");
//        };
//    }


}
