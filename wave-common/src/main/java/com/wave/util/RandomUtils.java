package com.wave.util;

import java.util.Random;

/**
 * Created by lijinyang on 2020/5/26.
 */
public class RandomUtils {

    /**
     * 获取随机值
     * @param max
     * @return
     */
    public static int getRandomInt(int max) {
        return (int) (Math.random() * max);
    }
}
