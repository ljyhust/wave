package com.wave.url.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ShortUrlGenRequest {
    
    @NotEmpty
    private String originalUrl;
}
