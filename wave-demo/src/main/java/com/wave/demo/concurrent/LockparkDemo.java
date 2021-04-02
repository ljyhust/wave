package com.wave.demo.concurrent;

import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport.park()理解
 */
public class LockparkDemo {
    
    public static void main(String[] args) {
    
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread-1 park...");
                LockSupport.park();
                System.out.println("thread-1 parked...");
            }
        }).start();
    
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread-2 park...");
                LockSupport.park();
                System.out.println("thread-2 parked...");
            }
        }).start();
        LockSupport.park();
    }
    
}
