package com.wave.demo.concurrent;

/**
 * synchronized monitor理解
 */
public class MonitorSync {
    
    static Object mutex = new Object();
    
    static volatile int value = 0;
    
    public static void incr() {
        synchronized (mutex) {
            value++;
        }
    }
    
    public static void dec() {
        synchronized (mutex) {
            value--;
        }
    }
    
    public static void wait_test() throws InterruptedException {
        synchronized (mutex) {
            System.out.println("wait_set " + Thread.currentThread().getId());
            mutex.wait();
            System.out.println("acquire condition " + Thread.currentThread().getId());
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
    
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MonitorSync.wait_test();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        };
        
        Thread.sleep(6000);
        synchronized (mutex) {
            MonitorSync.mutex.notifyAll();
            Thread.sleep(60);
            System.out.println("main call notify, release lock");
        }
        Thread.sleep(6000);
    }
}
