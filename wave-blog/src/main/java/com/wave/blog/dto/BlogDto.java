package com.wave.blog.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BlogDto implements Serializable {
    
    private static final long serialVersionUID = -6292943830028372631L;
    
    private Long id;
    
    private String content;
    
}
