package com.wave.common;

import lombok.Data;

/**
 * @author lijinyang
 */
@Data
public class RestResult<T>{

    private int code = 200;

    private String msg;

    private T data;
}
