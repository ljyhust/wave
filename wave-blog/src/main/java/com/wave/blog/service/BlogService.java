package com.wave.blog.service;

import com.wave.blog.dto.MessageBlogMqDto;
import com.wave.blog.dto.req.BlogChangeRequest;
import com.wave.common.TransactionMQClientResult;
import com.wave.exception.WaveException;

public interface BlogService {
    
    /**
     * 增加/修改帖子.
     * @param request
     * @throws WaveException
     * @return
     */
    TransactionMQClientResult blogEdit(BlogChangeRequest request, Long userId) throws WaveException;
    
    /**
     * delete blog by id.
     * @param blogId
     * @throws WaveException
     */
    void blogDelete(Long blogId, Long userId) throws WaveException;
    
    /**
     * consumer blog message.
     * @param messageBlogMqDto mq blog message.
     * @throws WaveException
     */
    boolean blogMQConsumerHandler(MessageBlogMqDto messageBlogMqDto) throws WaveException;
}
