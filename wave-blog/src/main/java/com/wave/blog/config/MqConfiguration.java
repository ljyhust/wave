package com.wave.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@DependsOn({"BlogMQProducerCheckListener"})
public class MqConfiguration {
    
    @Value("${rocket.namesrvAddr:}")
    private String rocketNamesrvAddr;
    
    /**
     * blog事务消息生产
     * @param listener 监听
     * @return
     */
    @Bean(name = "blogMQProducer", destroyMethod = "shutdown")
    public TransactionMQProducer blogTransactionMQProducer(
            @Autowired @Qualifier("blogMQProducerListen") TransactionListener listener) {
        TransactionMQProducer transactionMQProducer = new TransactionMQProducer("blog_transaction_group");
        // set thread info to response mq check commit request
        ThreadPoolExecutor blogMqCheckStateThread = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2048), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("blog_mq_check_state_thread");
                return thread;
            }
        });
        transactionMQProducer.setExecutorService(blogMqCheckStateThread);
        transactionMQProducer.setTransactionListener(listener);
        transactionMQProducer.setNamesrvAddr(rocketNamesrvAddr);
        try {
            transactionMQProducer.start();
        } catch (MQClientException e) {
            log.error("=====> blog mq producer error", e);
        }
        return transactionMQProducer;
    }
    
    /**
     * blog mq push consumer.
     * this push realized by poll pull service message
     * @return
     */
    @Bean(name = "blogMQConsumer", destroyMethod = "shutdown")
    public DefaultMQPushConsumer blogTransactionMQConsumer(
            @Autowired @Qualifier("blogMQConsumer") MessageListenerConcurrently listener) {
        DefaultMQPushConsumer blogTransactionConsumer = new DefaultMQPushConsumer(
                "blog_transaction_consumer_group");
        blogTransactionConsumer.setNamesrvAddr(rocketNamesrvAddr);
        try {
            blogTransactionConsumer.subscribe(WaveBlogConstants.WAVE_BLOG_MQ_TOPIC, "*");
            blogTransactionConsumer.setMessageListener(listener);
            blogTransactionConsumer.start();
        } catch (MQClientException e) {
            log.error("=====> blog mq consumer error", e);
        }
        return blogTransactionConsumer;
    }
}
