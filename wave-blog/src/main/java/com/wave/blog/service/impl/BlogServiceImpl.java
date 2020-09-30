package com.wave.blog.service.impl;

import com.wave.blog.dao.BlogDao;
import com.wave.blog.dao.entity.BlogEntity;
import com.wave.blog.dto.req.BlogChangeRequest;
import com.wave.blog.service.BlogService;
import com.wave.exception.WaveException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlogServiceImpl implements BlogService {
    
    @Autowired
    BlogDao blogDao;
    
    @Override
    public void blogEdit(BlogChangeRequest request, Long userId) throws WaveException {
        BlogEntity blogEntity = new BlogEntity();
        blogEntity.setUserId(userId);
        blogEntity.setContent(request.getContent());
        // generator id
        
        // mq notify blog change
        blogDao.insert(blogEntity);
    }
    
    @Override
    public void blogDelete(Long blogId, Long userId) throws WaveException {
    
    }
}
