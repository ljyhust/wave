package com.wave.trip;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringCloudApplication
@MapperScan(basePackages = {"com.wave.trip.dao"})
@EnableFeignClients
public class WaveTripApplication {
    public static void main(String[] args) {
        SpringApplication.run(WaveTripApplication.class);
    }
}
