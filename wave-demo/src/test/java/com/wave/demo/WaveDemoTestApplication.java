package com.wave.demo;

import com.wave.demo.config.RedissonConfig;
import com.wave.demo.redisopt.DelayQueueRedisDemo;
import com.wave.operator.DelayQueueRedisOpt;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WaveDemoApplication.class)
@Slf4j
public class WaveDemoTestApplication {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    /**
     * Redis延时队列demo
     * @throws Exception
     */
    @Test
    public void testRedisDelayQueue() throws Exception {
        DelayQueueRedisOpt delayQueueRedisOpt = new DelayQueueRedisOpt(redissonClient);
        DelayQueueRedisDemo queueRedisDemo = new DelayQueueRedisDemo("testKey", delayQueueRedisOpt, scheduledThreadPoolExecutor) {
            @Override
            public void task(Collection<Object> objects) {
                log.info("====> 消费完成 {}", objects);
            }
        };
        queueRedisDemo.start();
        RedisDelayQueueEntity queueEntity = new RedisDelayQueueEntity();
        queueEntity.setUserId(1342354L);
        queueEntity.setUserName("test111");
        queueRedisDemo.add(queueEntity);
        // 以下几个无法add进入，队列是Set集合，重复数据无法add
        queueRedisDemo.add(queueEntity);
        queueRedisDemo.add(queueEntity);
        queueRedisDemo.add(queueEntity);
        Thread.sleep(1000);

        queueRedisDemo.add(queueEntity);
        queueEntity.setUserName("test112");
        queueRedisDemo.add(queueEntity);
        queueRedisDemo.add(queueEntity);

        while (true) {
            Thread.sleep(1000);
        }
    }

}
