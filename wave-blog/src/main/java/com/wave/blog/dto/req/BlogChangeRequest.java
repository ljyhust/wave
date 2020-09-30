package com.wave.blog.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class BlogChangeRequest implements Serializable {
    
    private static final long serialVersionUID = 3665945234389038892L;
    
    private Long id;
    
    @NotBlank(message = "must not be null")
    private String content;
    
}
