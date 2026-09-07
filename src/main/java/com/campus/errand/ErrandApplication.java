package com.campus.errand;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.campus.errand.module")
public class ErrandApplication {
    public static void main(String[] args) {
        SpringApplication.run(ErrandApplication.class, args);
    }
}
