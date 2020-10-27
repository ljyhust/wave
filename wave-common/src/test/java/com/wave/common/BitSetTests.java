package com.wave.common;

import com.wave.consistency.id.DefaultServiceIdGenerator;
import com.wave.consistency.id.ServiceIdGenerator;
import com.wave.util.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.BitSet;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class BitSetTests {
    
    @Test
    public void testBit() throws Exception {
        long test = 1601467211456L;
        BitSet bitSet = new BitSet(64);
        for (int i = 64; i > 0; i--) {
            bitSet.set(i, test % 2 == 1);
            test = test >> 1L;
        }
        System.out.printf(bitSet.toString());
    
    }
    
    @Test
    public void testIdGenerator() throws Exception {
        DefaultServiceIdGenerator idGenerator = new DefaultServiceIdGenerator(4L);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(1000, new Runnable() {
            @Override
            public void run() {
                System.out.println("===> start");
            }
        });
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            idGererateThread(cyclicBarrier, idGenerator).start();
            //long l = idGenerator.nextId(RandomUtils.getRandomInt(10000));
            //System.out.println(l);
        }
        long end = System.currentTimeMillis();
        System.out.println("====> time cost " + (end - start));
        Thread.sleep(600_000L);
    }
    
    public Thread idGererateThread(CyclicBarrier cyclicBarrier, ServiceIdGenerator idGenerator) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    cyclicBarrier.await();
                } catch (Exception e) {
                    //log.error("====> exception ", e);
                    e.printStackTrace();
                    return;
                }
                for (int i = 0; i < 100; i++) {
                    long l = idGenerator.nextId(RandomUtils.getRandomInt(10000));
                }
                //log.info("====> id is {}", l);
                //System.out.println(l);
            }
        });
    }
    
    @Test
    public void testLeft() {
        System.out.println(654 << 0);
    }
    
}
