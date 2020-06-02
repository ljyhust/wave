package com.wave.demo.redisopt;

import com.wave.operator.DelayQueueRedisOpt;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public abstract class DelayQueueRedisDemo{

    private String key;

    private DelayQueueRedisOpt delayQueueRedisOpt;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    public DelayQueueRedisDemo(String key,
                               DelayQueueRedisOpt delayQueueRedisOpt,
                               ScheduledThreadPoolExecutor scheduledThreadPoolExecutor) {
        this.key = key;
        this.delayQueueRedisOpt = delayQueueRedisOpt;
        this.scheduledThreadPoolExecutor = scheduledThreadPoolExecutor;
    }

    public void start() throws Exception {
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                long currentTimeMillis = System.currentTimeMillis();
                Collection<Object> objects = delayQueueRedisOpt.pollByScore(key, 0, currentTimeMillis);
                // TODO handle
                log.info("=====> 取出消费数据  {} ", objects);
                task(objects);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public abstract void task(Collection<Object> objects);

    public DelayQueueRedisOpt getDelayQueueRedisOpt() {
        return this.delayQueueRedisOpt;
    }

    public void add(Object obj) {
        delayQueueRedisOpt.add(key, obj, Double.valueOf(System.currentTimeMillis()));
    }
}
