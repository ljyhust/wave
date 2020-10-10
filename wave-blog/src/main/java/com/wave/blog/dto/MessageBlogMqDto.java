package com.wave.blog.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;

@Data
public class MessageBlogMqDto implements Serializable {
    
    private static final long serialVersionUID = -18498644257100949L;
    
    private Long blogId;
    
    private String content;
    
    private String operationType;
    
    private Long userId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTs;
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
