package com.wave.blog.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_blog")
public class BlogEntity {
    
    private Long id;
    
    private String content;
    
    private Long userId;
    
    private Date createTs;
    
    private Short status;
}
