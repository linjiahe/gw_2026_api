package com.gw.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.gw.server.mapper")
@EnableScheduling
public class GwServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GwServerApplication.class, args);
    }
}
