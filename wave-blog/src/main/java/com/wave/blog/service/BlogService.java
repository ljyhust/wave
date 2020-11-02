package com.wave.blog.service;

import com.wave.blog.dao.entity.BlogEntity;
import com.wave.blog.dto.BlogDto;
import com.wave.blog.dto.MessageBlogMqDto;
import com.wave.blog.dto.req.BlogChangeRequest;
import com.wave.common.PageVo;
import com.wave.common.TransactionMQClientResult;
import com.wave.exception.WaveException;

public interface BlogService {
    
    /**
     * 查询我发表的blog
     * @param pageIndex 页引
     * @param pageSize 页大小
     * @param userId 用户id
     * @return
     * @throws WaveException
     */
    PageVo<BlogDto> queryMyBlog(Integer pageIndex, Integer pageSize, Long userId) throws WaveException;
    
    /**
     * 查询关注的帖子
     * @param pageIndex 页索引
     * @param pageSize 页大小
     * @param userId 登录者用户id
     * @return
     * @throws WaveException
     */
    PageVo<BlogDto> queryFocusBlog(Integer pageIndex, Integer pageSize, Long userId) throws WaveException;
    
    /**
     * 根据id查询帖子内容，入redis缓存
     * @param id
     * @return
     * @throws WaveException
     */
    BlogEntity queryBlogById(Long id) throws WaveException;
    
    /**
     * 编辑我的帖子
     * @param id 帖子id
     * @param userId 创建者
     * @throws WaveException
     */
    void blogEdit(BlogChangeRequest request, Long userId) throws WaveException;
    
    /**
     * 增加/修改帖子.
     * @param request
     * @throws WaveException
     * @return
     */
    TransactionMQClientResult blogPublish(BlogChangeRequest request, Long userId) throws WaveException;
    
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
