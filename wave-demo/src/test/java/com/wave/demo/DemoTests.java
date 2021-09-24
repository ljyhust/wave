package com.wave.demo;

import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lijinyang
 * @date 2021/8/27 19:38
 */
public class DemoTests {

    @Test
    public void testSort() {
        List<Integer> temp = Arrays.asList(4, 9, 2, 43, 6, 18, 27);
        List<Integer> result = temp.stream().sorted((o1, o2) -> {
            return o2 - o1;
        }).collect(Collectors.toList());
        System.out.println("=== result " + result);
    }
}
