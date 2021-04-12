package com.wave.url;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * wave url application
 * 短链接系统
 */
@SpringCloudApplication
@MapperScan(basePackages = {"com.wave.url.dao"})
@EnableAspectJAutoProxy(exposeProxy = true)
public class WaveUrlApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(WaveUrlApplication.class, args);
    }
}
