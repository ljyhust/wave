package com.wave.blog.service;

import com.alibaba.fastjson.JSONObject;
import com.wave.blog.config.WaveBlogConstants;
import com.wave.blog.dao.BlogDao;
import com.wave.blog.dao.entity.BlogEntity;
import com.wave.blog.dto.MessageBlogMqDto;
import com.wave.common.WaveConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service("blogMQProducerListen")
public class BlogMQProducerTransactionListener implements TransactionListener {
    
    @Autowired
    BlogDao blogDao;
    
    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        // 返回unknow 问题：带来事务查询开销，优点是：减小业务代码重复
        // 1. 解析message
        String blogMsgType = o.toString();
        // 2. 处理不同Tag的 message执行
        try {
            String s = new String(message.getBody(), RemotingHelper.DEFAULT_CHARSET);
            switch (blogMsgType) {
                case WaveBlogConstants.WAVE_BLOG_MSG_TYPE_NEW:
                    MessageBlogMqDto messageBlogMqDto = JSONObject.parseObject(s, MessageBlogMqDto.class);
                    BlogEntity blogEntity = new BlogEntity();
                    blogEntity.setId(messageBlogMqDto.getBlogId());
                    blogEntity.setContent(messageBlogMqDto.getContent());
                    blogEntity.setUserId(messageBlogMqDto.getUserId());
                    blogEntity.setCreateTs(messageBlogMqDto.getCreateTs());
                    blogDao.insert(blogEntity);
                    log.info("=====> local transaction execute ok {}", message);
                    break;
                case WaveBlogConstants.WAVE_BLOG_MSG_TYPE_DELELTE:
                    MessageBlogMqDto messageBlogMqDtoDEL = JSONObject.parseObject(s, MessageBlogMqDto.class);
                    BlogEntity blogEntityDel = new BlogEntity();
                    blogEntityDel.setId(messageBlogMqDtoDEL.getBlogId());
                    blogEntityDel.setStatus(WaveConstants.DEL_STATUS);
                    blogDao.updateById(blogEntityDel);
                    log.info("=====> local transaction execute ok {}", message);
                    break;
                default:
                    return LocalTransactionState.UNKNOW;
            }
        } catch (UnsupportedEncodingException e) {
            log.error("=====> local transaction execute error ", e);
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        // 3. 根据执行结果返回不同state
        return LocalTransactionState.COMMIT_MESSAGE;
    }
    
    /**
     * blog mq message commit status check.
     * @param messageExt message body
     * @return state
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        try {
            String s = new String(messageExt.getBody(), RemotingHelper.DEFAULT_CHARSET);
            MessageBlogMqDto messageBlogMqDto = JSONObject.parseObject(s, MessageBlogMqDto.class);
            BlogEntity blogEntity = blogDao.selectById(messageBlogMqDto.getBlogId());
            if (null == blogEntity) {
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return LocalTransactionState.UNKNOW;
        }
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
