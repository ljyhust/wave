package com.wave.demo.config;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * Created by lijinyang on 2020/5/27.
 */
@Configuration
public class ThreadExcutorPoolConfig {

    @Bean
    public ScheduledThreadPoolExecutor scheduledThreadPool() {
        ScheduledThreadPoolExecutor customThread = new ScheduledThreadPoolExecutor(5,
                new DefaultThreadFactory("customThread"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        customThread.setMaximumPoolSize(10);
        customThread.setKeepAliveTime(1, TimeUnit.SECONDS);
        return customThread;
    }
}
