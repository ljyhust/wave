package com.wave.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    
    /**
     * blog事务消息
     * @param listener 监听
     * @return
     */
    @Bean(name = "blogMQProducer")
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
        transactionMQProducer.setNamesrvAddr("localhost:9786");
        try {
            transactionMQProducer.start();
        } catch (MQClientException e) {
            log.error("=====> blog mq producer error", e);
        }
        return transactionMQProducer;
    }
}
