package com.wave.demo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class LocalSupportTests {

    @Test
    public void testLock() throws InterruptedException {

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
                2, 3, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));

        List<Thread> threads = new ArrayList<>();

        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    System.out.println(">>>> park ...");
                    threads.add(Thread.currentThread());
                    LockSupport.park();
                    System.out.println(">>>> wake up");

                    Thread.sleep(2000);
                    System.out.println(">>>> park again ...");
                    LockSupport.park();

                    System.out.println(">>>> wake up again ...");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread.sleep(10000);
        System.out.println("====> unpark");
        LockSupport.unpark(threads.get(0));


        Thread.sleep(10000);
        System.out.println(">>>> unpark again");
        LockSupport.unpark(threads.get(0));
    }
}
