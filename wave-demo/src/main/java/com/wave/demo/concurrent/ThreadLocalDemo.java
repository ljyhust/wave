package com.wave.demo.concurrent;

import java.util.Map;

public class ThreadLocalDemo {

    public static final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    public static ThreadLocal<Map<String, Object>> getThreadLocal() {
        return threadLocal;
    }

}
