package com.wave.demo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RedisDelayQueueEntity implements Serializable{

    private static final long serialVersionUID = 1563582949356729226L;
    private Long userId;

    private String userName;
}
