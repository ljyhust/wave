package com.wave.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wave.blog.config.WaveBlogConstants;
import com.wave.blog.dao.BlogDao;
import com.wave.blog.dao.entity.BlogEntity;
import com.wave.blog.dto.MessageBlogMqDto;
import com.wave.blog.dto.req.BlogChangeRequest;
import com.wave.blog.rpc.WaveUserFeignClient;
import com.wave.blog.service.BlogService;
import com.wave.common.PublicResponseObjDto;
import com.wave.common.TransactionMQClientResult;
import com.wave.common.WaveConstants;
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
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Service
public class BlogServiceImpl implements BlogService {
    
    private final String FANCY_BLOG_REDIS_PREFIX = "FANCY_BLOG";
    
    @Autowired
    BlogDao blogDao;
    @Autowired
    @Qualifier("blogMQProducer")
    TransactionMQProducer blogMQProducer;
    @Autowired
    WaveUserFeignClient userFeignClient;
    @Autowired
    RedissonClient redisson;
    
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
        // validate userId & blogId
        BlogEntity blogEntity = blogDao.selectById(blogId);
        if (null == blogEntity || !userId.equals(blogEntity.getUserId())
                || WaveConstants.NORMAL_STATUS != (short)blogEntity.getStatus()) {
            log.info("====> blog {} not exist");
            throw new WaveException(WaveException.INVALID_PARAM, "invalid param, no blog query");
        }
        MessageBlogMqDto messageBlogMqDto = new MessageBlogMqDto();
        messageBlogMqDto.setBlogId(blogId);
        messageBlogMqDto.setUserId(userId);
        messageBlogMqDto.setCreateTs(blogEntity.getCreateTs());
        messageBlogMqDto.setOperationType(WaveBlogConstants.WAVE_BLOG_MSG_TYPE_DELELTE);
    
        try {
            Message message = new Message(WaveBlogConstants.WAVE_BLOG_MQ_TOPIC, WaveBlogConstants.MQ_TAG_WAVE_BLOG_MSG_DELETE,
                    JSON.toJSONString(messageBlogMqDto).getBytes(RemotingHelper.DEFAULT_CHARSET));
            TransactionSendResult result = blogMQProducer.sendMessageInTransaction(message, WaveBlogConstants.WAVE_BLOG_MSG_TYPE_DELELTE);
            log.info("=====> transaction blog mq {}  result {}", messageBlogMqDto, result);
            if (result.getLocalTransactionState() == LocalTransactionState.ROLLBACK_MESSAGE) {
                throw new WaveException(WaveException.SERVER_ERROR, "发送失败");
            }
            if (result.getLocalTransactionState() == LocalTransactionState.UNKNOW) {
                throw new WaveException(201, "发送中...");
            }
    
            SendStatus sendStatus = result.getSendStatus();
            if (sendStatus != SendStatus.SEND_OK) {
                throw new WaveException(WaveException.OVER_THRESHOLD, "server is busy");
            }
        } catch (UnsupportedEncodingException | MQClientException e) {
            e.printStackTrace();
            throw new WaveException(WaveException.OVER_THRESHOLD, "server is busy");
        }
    }
    
    @Override
    public boolean blogMQConsumerHandler(MessageBlogMqDto messageBlogMqDto) throws WaveException {
        Long userId = messageBlogMqDto.getUserId();
        Date createTs = messageBlogMqDto.getCreateTs();
        // rpc wave-user fansUserIdList
        PublicResponseObjDto dto = userFeignClient.myFancyUserIdList(userId);
        if (dto.getCode() != 200) {
            log.info("====> feign request error {}", messageBlogMqDto);
            return false;
        }
        // redis zset fans_blog:userId blogId createTs
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(dto.getData()));
        JSONArray fancyUserIdList = jsonObject.getJSONArray("fancyUserIdList");
        switch (messageBlogMqDto.getOperationType()) {
            case WaveBlogConstants.WAVE_BLOG_MSG_TYPE_NEW:
                long score = createTs.getTime();
                // TODO 循环是否有优化空间，优化网络
                for (Object o : fancyUserIdList) {
                    redisson.getScoredSortedSet(WaveBlogConstants.FANCY_BLOG_REDIS_PREFIX + ":" + o.toString())
                            .addAsync(score, messageBlogMqDto.getBlogId());
                }
                break;
            case WaveBlogConstants.WAVE_BLOG_MSG_TYPE_DELELTE:
                for (Object o : fancyUserIdList) {
                    redisson.getScoredSortedSet(WaveBlogConstants.FANCY_BLOG_REDIS_PREFIX + ":" + o.toString())
                            .removeAsync(messageBlogMqDto.getBlogId().toString());
                }
                break;
            default:
                break;
        }
        return true;
    }
}
