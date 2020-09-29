package com.wave.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

@SpringCloudApplication
@MapperScan(basePackages = "com.wave.log")
public class WaveBlogApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(WaveBlogApplication.class, args);
    }
}
