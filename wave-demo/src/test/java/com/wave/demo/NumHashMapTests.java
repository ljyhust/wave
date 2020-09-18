package com.wave.demo;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class NumHashMapTests {

    /**
     * Long 与 Integer hashcode 相同，但共同入HashMap时，会认为是hash冲突，因为不满足 equals
     * equals判断：this == obj ?  即判断指针
     */
    @Test
    public void testMap() {
        Map<Integer, Object> map = new HashMap<>();

        map.put(Integer.valueOf(12345), new Object());

        System.out.println(map.get(Long.valueOf(12345)));
    }
}
