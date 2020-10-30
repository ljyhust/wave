package com.wave.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringCloudApplication
@ComponentScan(basePackages = {"com.wave", "com.wave.blog"})
@MapperScan(basePackages = {"com.wave.blog.dao"})
@EnableFeignClients
public class WaveBlogApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(WaveBlogApplication.class, args);
    }
}
