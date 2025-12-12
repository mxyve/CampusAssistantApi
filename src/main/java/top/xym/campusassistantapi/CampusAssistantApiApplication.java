package top.xym.campusassistantapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.xym.starter.common.annotation.EnableMqxuCommon;

@SpringBootApplication
@EnableMqxuCommon
public class CampusAssistantApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusAssistantApiApplication.class, args);
        System.out.println("Campus Assistant API 启动成功!");
        System.out.println("Knife4j 接口文档地址: http://localhost:6060/doc.html");
    }

}
