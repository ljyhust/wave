package com.wave.blog.controllers;

import com.wave.blog.dto.BlogDto;
import com.wave.blog.dto.req.BlogChangeRequest;
import com.wave.blog.service.BlogService;
import com.wave.common.PageQueryRequestDto;
import com.wave.common.PageVo;
import com.wave.common.PublicResponseDto;
import com.wave.common.PublicResponseObjDto;
import com.wave.common.PublicResponseUtil;
import com.wave.common.TransactionMQClientResult;
import com.wave.exception.WaveException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("blog")
public class BlogController {
    
    @Autowired
    BlogService blogService;
    
    /**
     * 编辑我的blog
     * @param requestVo
     * @param userId
     * @return
     * @throws WaveException
     */
    @PostMapping("editMyBlog")
    public PublicResponseDto editMyBlogContent(@RequestBody BlogChangeRequest requestVo,
            @RequestHeader("userId") Long userId) throws WaveException{
        if(null == requestVo.getId()) {
            throw new WaveException(WaveException.INVALID_PARAM, "id must not be null");
        }
        blogService.blogEdit(requestVo, userId);
        return PublicResponseUtil.publicResponseDto();
    }
    
    /**
     * 发表blog
     * @param request
     * @return
     * @throws WaveException
     */
    @PostMapping("publishMyBlog")
    public PublicResponseObjDto publishBlogContent(@RequestBody BlogChangeRequest requestVo,
            @RequestHeader("userId") Long userId) throws WaveException {
        TransactionMQClientResult result = blogService.blogPublish(requestVo, userId);
        return PublicResponseUtil.okPublicResponseObjDto(result);
    }
    
    @PostMapping("deleteMyBlog/{blogId}")
    public PublicResponseDto deleteMyBlogContent(@PathVariable("blogId") Long blogId,
            @RequestHeader("userId") Long userId) throws WaveException {
        blogService.blogDelete(blogId, userId);
        return PublicResponseUtil.publicResponseDto();
    }
    
    /**
     * 查询发表的blog
     * @param pageQueryRequestDto
     * @param userId
     * @return
     * @throws WaveException
     */
    @PostMapping("myBlog")
    public PublicResponseObjDto queryMyBlog(@RequestBody PageQueryRequestDto pageQueryRequestDto, @RequestHeader("userId") Long userId) throws WaveException {
        PageVo<BlogDto> blogDtoPageVo = blogService
                .queryMyBlog(pageQueryRequestDto.getPageIndex(), pageQueryRequestDto.getPageSize(), userId);
        return PublicResponseUtil.okPublicResponseObjDto(blogDtoPageVo);
    }
    
    /**
     * 查询关注的blog
     * @param pageQueryRequestDto 分页参数
     * @param userId 登录者id
     * @return
     * @throws WaveException
     */
    @PostMapping("focusBlog")
    public PublicResponseObjDto queryFocusUserBlogs(@RequestBody PageQueryRequestDto pageQueryRequestDto,
            @RequestHeader("userId") Long userId) throws WaveException {
        PageVo<BlogDto> blogDtoPageVo = blogService
                .queryFocusBlog(pageQueryRequestDto.getPageIndex(), pageQueryRequestDto.getPageSize(), userId);
        return PublicResponseUtil.okPublicResponseObjDto(blogDtoPageVo);
    }
}
