package com.gw.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gw.server.mapper")
public class GwServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GwServerApplication.class, args);
    }
}
