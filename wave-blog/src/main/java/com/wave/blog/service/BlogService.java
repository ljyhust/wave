package com.wave.blog.service;

import com.wave.blog.dto.req.BlogChangeRequest;
import com.wave.exception.WaveException;

public interface BlogService {
    
    /**
     * 增加/修改帖子.
     * @param request
     * @throws WaveException
     */
    void blogEdit(BlogChangeRequest request, Long userId) throws WaveException;
    
    /**
     * delete blog by id.
     * @param blogId
     * @throws WaveException
     */
    void blogDelete(Long blogId, Long userId) throws WaveException;
}
