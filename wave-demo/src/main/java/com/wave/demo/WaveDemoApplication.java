package com.wave.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class WaveDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaveDemoApplication.class, args);
    }
}
