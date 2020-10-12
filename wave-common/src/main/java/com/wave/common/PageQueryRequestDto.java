package com.wave.common;

import lombok.Data;

/**
 * @author lijinyang
 */
@Data
public class PageQueryRequestDto {
    
    private Integer pageIndex = 1;
    
    /**
     * 默认一页20条记录
     */
    private Integer pageSize = 20;
}
