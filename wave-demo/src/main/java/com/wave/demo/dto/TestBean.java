package com.wave.demo.dto;

import lombok.Data;

/**
 * @author lijinyang
 * @date 2021/7/22 15:47
 */
@Data
public class TestBean {

    private String testStr;

    private Integer id;

    public TestBean(String testStr, Integer id) {
        this.testStr = testStr;
        this.id = id;
    }
}
