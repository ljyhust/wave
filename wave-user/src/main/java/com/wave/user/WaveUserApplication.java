package com.wave.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringCloudApplication
@MapperScan(basePackages = {"com.wave.user.dao"})
public class WaveUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaveUserApplication.class, args);
    }
}
