package com.wave.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lijinyang
 */
@Data
public class RestResult<T> implements Serializable{

    private static final long serialVersionUID = 7448370920363826630L;
    
    private int code = 200;

    private String msg;

    private T data;
}
