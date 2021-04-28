package com.wave.file;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringCloudApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class WaveFileApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(WaveFileApplication.class, args);
    }
}
