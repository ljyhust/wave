package com.wave.url.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_url_id")
public class UrlIdEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long startNum;
    
    private Long endNum;
    
    private LocalDateTime createTs;
    
    private LocalDateTime updateTs;
    
    private Short status;
}
