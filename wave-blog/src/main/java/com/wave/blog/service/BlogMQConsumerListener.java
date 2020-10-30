package com.wave.blog.service;

import com.alibaba.fastjson.JSONObject;
import com.wave.blog.config.WaveBlogConstants;
import com.wave.blog.dto.MessageBlogMqDto;
import com.wave.blog.rpc.WaveUserFeignClient;
import com.wave.exception.WaveException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * blog 消息消费处理
 */
@Slf4j
@Service("blogMQConsumerListen")
public class BlogMQConsumerListener implements MessageListenerConcurrently {
    
    @Autowired
    BlogService blogService;
    
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,
            ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        log.info("=====> receive mesgs {}", list);
        for (MessageExt messageExt : list) {
            // message解析
            try {
                String s = new String(messageExt.getBody(), RemotingHelper.DEFAULT_CHARSET);
                MessageBlogMqDto messageBlogMqDto = JSONObject.parseObject(s, MessageBlogMqDto.class);
                boolean res = blogService.blogMQConsumerHandler(messageBlogMqDto);
                if (!res) {
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            } catch (UnsupportedEncodingException | WaveException e) {
                log.error("====> message {} code error {}", messageExt.getMsgId(), e);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
    
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
