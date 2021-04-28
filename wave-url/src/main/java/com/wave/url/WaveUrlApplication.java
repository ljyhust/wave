package com.wave.url;

import com.wave.url.config.GracefulShutdown;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Bean;
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
    
    @Bean
    public ConfigurableServletWebServerFactory webServerFactory(final GracefulShutdown gracefulShutdown) {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(gracefulShutdown);
        return factory;
    }
}
