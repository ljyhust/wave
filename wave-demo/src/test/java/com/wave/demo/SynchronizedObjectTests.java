package com.wave.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;

@Slf4j
public class SynchronizedObjectTests {

    // jvm启动创建
    private static Object obj = new Object();

    public static void main(String[] args) throws InterruptedException {

        // 无锁 001
        log.info(ClassLayout.parseInstance(obj).toPrintable());

        synchronized (obj) {
            // 轻量级锁 000
            log.info(ClassLayout.parseInstance(obj).toPrintable());
        }

        // 锁释放
        log.info(ClassLayout.parseInstance(obj).toPrintable());

        log.info("===== 测试偏向锁 ");
        Thread.sleep(10000);
        // 偏向锁 101, JVM启动4s后会启动，创建的对象默认是偏向锁
        Object obj2 = new Object();
        log.info(ClassLayout.parseInstance(obj2).toPrintable());

        synchronized (obj2) {
            log.info(ClassLayout.parseInstance(obj2).toPrintable());
        }

        log.info(ClassLayout.parseInstance(obj2).toPrintable());

    }
}
