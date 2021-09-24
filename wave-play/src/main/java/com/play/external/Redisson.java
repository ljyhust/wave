package com.play.external;

import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zz
 * @date 2021/8/30 16:26
 */
@Configuration
public class Redisson {
    @Value("${redis.address:}")
    private String redisAddress;

    @Value("${redis.timeout:2000}")
    private int redisTimeout;

    @Value("${redis.connectTimeout:1000}")
    private int connectTimeout;

    @Value("${redis.passwd:}")
    private String redisPasswd;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient getRedisClient() {
        Config config = new Config();
        config.setTransportMode(TransportMode.NIO);
        config.useSingleServer().setAddress(redisAddress).setTimeout(redisTimeout)
                .setPassword(redisPasswd).setRetryAttempts(2).setConnectTimeout(connectTimeout).setKeepAlive(true).setPingConnectionInterval(1000);
        return org.redisson.Redisson.create(config);
    }
}
