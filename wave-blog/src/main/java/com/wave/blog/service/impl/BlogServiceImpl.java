package com.wave.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wave.blog.config.WaveBlogConstants;
import com.wave.blog.dao.BlogDao;
import com.wave.blog.dto.MessageBlogMqDto;
import com.wave.blog.dto.req.BlogChangeRequest;
import com.wave.blog.service.BlogService;
import com.wave.common.TransactionMQClientResult;
import com.wave.consistency.id.IdGeneratorManager;
import com.wave.exception.WaveException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Slf4j
@Service
public class BlogServiceImpl implements BlogService {
    
    @Autowired
    BlogDao blogDao;
    @Autowired
    @Qualifier("blogMQProducer")
    TransactionMQProducer blogMQProducer;
    
    @Override
    public TransactionMQClientResult blogEdit(BlogChangeRequest request, Long userId) throws WaveException {
        // generator id
        long id = IdGeneratorManager.getGenerator(WaveBlogConstants.WAVE_BLOG_NAME).nextId(userId);
        MessageBlogMqDto messageBlogMqDto = new MessageBlogMqDto();
        messageBlogMqDto.setBlogId(id);
        messageBlogMqDto.setContent(request.getContent());
        messageBlogMqDto.setCreateTs(new Date());
        messageBlogMqDto.setUserId(userId);
        messageBlogMqDto.setOperationType(WaveBlogConstants.WAVE_BLOG_MSG_TYPE_NEW);
        // mq notify blog change with half transaction
        try {
            Message message = new Message(WaveBlogConstants.WAVE_BLOG_MQ_TOPIC, WaveBlogConstants.MQ_TAG_WAVE_BLOG_MSG_NEW,
                    JSON.toJSONString(messageBlogMqDto).getBytes(RemotingHelper.DEFAULT_CHARSET));
            TransactionSendResult result = blogMQProducer.sendMessageInTransaction(message, WaveBlogConstants.WAVE_BLOG_MSG_TYPE_NEW);
            log.info("=====> transaction blog mq {}  result {}", messageBlogMqDto, result);
            if (result.getLocalTransactionState() == LocalTransactionState.ROLLBACK_MESSAGE) {
                throw new WaveException(WaveException.SERVER_ERROR, "发送失败");
            }
            if (result.getLocalTransactionState() == LocalTransactionState.UNKNOW) {
                TransactionMQClientResult transactionResult = new TransactionMQClientResult();
                transactionResult.setCode(1);
                transactionResult.setTimeout(60000);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("blogId", messageBlogMqDto.getBlogId());
                transactionResult.setData(jsonObject);
                return transactionResult;
            }
            
            SendStatus sendStatus = result.getSendStatus();
            if (sendStatus != SendStatus.SEND_OK) {
                throw new WaveException(WaveException.OVER_THRESHOLD, "server is busy");
            }
            
        } catch (UnsupportedEncodingException | MQClientException e) {
            e.printStackTrace();
            throw new WaveException(WaveException.OVER_THRESHOLD, "server is busy");
        }
        TransactionMQClientResult result = new TransactionMQClientResult();
        messageBlogMqDto.setContent(null);
        result.setData(messageBlogMqDto);
        return result;
    }
    
    @Override
    public void blogDelete(Long blogId, Long userId) throws WaveException {
    
    }
}
