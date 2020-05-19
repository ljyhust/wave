package com.wave.base;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = {"com.wave.base.dao"})
public class WaveBaseApplication {

    public static void main(String[] args) {
        for (String arg : args) {
            log.info("=====> {} ", arg);
        }
        SpringApplication.run(WaveBaseApplication.class, args);
    }
}
