package com.wave.url.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("t_short_url")
public class ShortUrlEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String originalUrl;
    
    private String md5;
    
    private Date createTs;
    
    private Date updateTs;
    
    @TableLogic
    private Short status;
}
