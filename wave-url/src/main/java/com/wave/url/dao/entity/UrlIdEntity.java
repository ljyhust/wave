package com.wave.url.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("t_url_id")
public class UrlIdEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long startNum;
    
    private Long endNum;
    
    private Date createTs;
    
    private Date updateTs;
    
    private Short status;
}
