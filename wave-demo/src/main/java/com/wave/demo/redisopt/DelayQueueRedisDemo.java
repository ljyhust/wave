package com.wave.demo.redisopt;

import com.wave.operator.DelayQueueRedisOpt;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by lijinyang on 2020/5/27.
 */
@Service
public class DelayQueueRedisDemo implements InitializingBean{

    private String key;

    @Autowired
    DelayQueueRedisOpt delayQueueRedisOpt;

    @Autowired
    ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                long currentTimeMillis = System.currentTimeMillis();
                Collection<Object> objects = delayQueueRedisOpt.pollByScore(key, 0, currentTimeMillis);
                // TODO handle
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
}
