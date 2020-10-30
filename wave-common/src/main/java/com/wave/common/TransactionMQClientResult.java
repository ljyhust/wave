package com.wave.common;

import lombok.Data;

import java.io.Serializable;

/**
 * MQ事务消息客户端返回
 */
@Data
public class TransactionMQClientResult<T> implements Serializable {
    
    /**
     * 0-正常  1-处理中  -1异常
     */
    private int transactionCode = 0;
    
    /**
     * 超时时间，用于code=1时，client轮询时间处理
     * millseconds
     */
    private long timeout = -1L;
    
    private T data;
}
