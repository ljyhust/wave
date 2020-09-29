package com.wave.common;

import lombok.Data;

import java.util.List;

/**
 * @author lijinyang
 */
@Data
public class PageVo<T> {
    private Integer pageIndex = 1;
    
    private Integer pageSize = 20;
    
    /**
     * 总页数
     */
    private Integer pageCount = 0;
    
    /**
     * 总记录数
     */
    private Integer total = 0;
    
    private List<T> rows;
}
