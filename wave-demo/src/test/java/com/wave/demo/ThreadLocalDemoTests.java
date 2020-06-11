package com.wave.demo;

import com.alibaba.fastjson.JSON;
import com.wave.demo.concurrent.ThreadLocalDemo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Local使用方法，线程都获取local查看是否有值
 */
@Slf4j
public class ThreadLocalDemoTests{

    public static void main(String[] args) {
        // 创建线程
        Thread thread1 = new Thread(() -> {
            ThreadLocal<Map<String, Object>> threadLocal = ThreadLocalDemo.getThreadLocal();
            Map<String, Object> map = threadLocal.get();
            if (null == map) {
                threadLocal.set(new HashMap<>());
                log.info("====> thread 1 no threadLocal");
            }
            threadLocal.get().put("thread-1", "this threadLocalMap of thread - 1");
            log.info("====> thread 1 threadLocal is {}", JSON.toJSONString(threadLocal.get()));
        }, "thread-1");

        Thread thread2 = new Thread(() -> {
            ThreadLocal<Map<String, Object>> threadLocal = ThreadLocalDemo.getThreadLocal();
            Map<String, Object> map = threadLocal.get();
            if (null == map) {
                threadLocal.set(new HashMap<>());
                log.info("====> thread 2 no threadLocal");
            }
            threadLocal.get().put("thread-2", "this threadLocalMap of thread - 2");
            log.info("====> thread 2 threadLocal is {}", JSON.toJSONString(threadLocal.get()));
        }, "thread-2");

        Thread thread3 = new Thread(() -> {
            ThreadLocal<Map<String, Object>> threadLocal = ThreadLocalDemo.getThreadLocal();
            Map<String, Object> map = threadLocal.get();
            if (null == map) {
                threadLocal.set(new HashMap<>());
                log.info("====> thread 3 no threadLocal");
            }
            threadLocal.get().put("thread-3", "this threadLocalMap of thread - 3");
            log.info("====> thread 3 threadLocal is {}", JSON.toJSONString(threadLocal.get()));
        }, "thread-3");

        thread1.start();
        thread2.start();
        thread3.start();
    }

    @Test
    public void testGetCpuNum() throws Exception {
        int n = Runtime.getRuntime().availableProcessors();
        log.info("====> cpu nums is {}", n);
    }

}
