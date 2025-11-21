package com.mengyo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 萌友益站 - 应用启动类
 * 
 * @author Mengyo Team
 * @since 2025-11-21
 */
@SpringBootApplication
@MapperScan("com.mengyo.module.*.mapper")
public class MengyoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MengyoApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("    萌友益站后端服务启动成功！");
        System.out.println("    API文档地址: http://localhost:8080/api/doc.html");
        System.out.println("========================================\n");
    }

}
