package com.wave.trip.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SunlandsProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 行程订单配置
 */
@Slf4j
@Configuration
public class TripOrderProducerConfig {

    @Value("${rocketmq.appId}")
    private String appId;

    @Value("${rocketmq.secretKey}")
    private String secretKey;

    @Value("${rocketmq.nameServer}")
    private String nameServer;

    /**
     * 多个以,分隔，在发送时用Message中的topic指定要发送的topic
     */
    @Value("${rocketmq.tripOrder.topic}")
    private String tripOrderTopic;

    @Value("${rocketmq.tripOrder.producerGroup}")
    private String tripOrderProducerGroup;

    @Value("${rocketmq.tripOrder.messageType}")
    private String tripOrderMessageType;

    @Bean(name = "tripOrderProducer", destroyMethod = "destroy")
    public SunlandsProducer tripOrderProducer() {
        SunlandsProducer producer = new SunlandsProducer();
        producer.setAppId(appId);
        producer.setNameServer(nameServer);
        producer.setSecretKey(secretKey);
        producer.setTopic(tripOrderTopic);
        producer.setMessageType(tripOrderMessageType);
        producer.setProducerGroup(tripOrderProducerGroup);
        producer.setMessageSendRetryCount(2);
        try {
            producer.init();
            log.info("====> 启动 {} MQ生产者成功", appId);
        } catch (MQClientException e) {
            log.error("=====>  启动 {} MQ生产者出错 {}", appId, e);
        }
        return producer;
    }
}
