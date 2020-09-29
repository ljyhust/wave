package com.wave.blog.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_blog_comment")
public class BlogCommentEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long blogId;
    
    private String comment;
    
    private Long userId;
    
    private Short status;
}
