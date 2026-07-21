package com.education.store;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 新艺智源管理平台启动类
 */
@EnableAsync
@SpringBootApplication
@MapperScan("com.education.store.**.mapper")
@ServletComponentScan(basePackages = "com.education.store.common.filter")
public class EduApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduApplication.class, args);
        System.out.println("=======================================");
        System.out.println("  新艺智源管理平台启动成功！");
        System.out.println("  API文档: http://localhost:8080/doc.html");
        System.out.println("=======================================");
    }
}
