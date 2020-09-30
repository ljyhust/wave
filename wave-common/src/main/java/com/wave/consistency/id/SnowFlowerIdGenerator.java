package com.wave.consistency.id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 雪花算法实现
 * 1个符号位 + 41 bit时间戳 + 10位工作进程bit + 12 bit 序列号
 * 12 bit序列号 = customServiceIdBit + 序列号 sequence
 */
public class SnowFlowerIdGenerator implements IdGenerator{
    
    /**
     * Start time intercept (2020/9/30 20:00:00)
     * 占41位
     */
    public static final long EPOCH = 1601460000000L;
    
    private static final Logger logger = LoggerFactory.getLogger(SnowFlowerIdGenerator.class);
    
    /**
     * 序列号位
     */
    private static final long SEQUENCE_BITS = 12L;
    
    private static final long MAX_WORKER_ID = 1024L;
    
    private static final long CUSTOM_SERVICE_BIT_MAX = 8L;
    
    /**
     * 业务id bit位数
     */
    private long customServiceIdBits = 0L;
    
    private long maxSequence = 0L;
    
    private long workerId = -1L;
    private long lastTimestamp = -1L;
    
    /**
     * 系统序列号
     */
    private long sequence = 0L;
    
    public SnowFlowerIdGenerator() {
        init();
    }
    
    public SnowFlowerIdGenerator(long customServiceIdBits) {
        this(customServiceIdBits, -1L);
    }
    
    public SnowFlowerIdGenerator(long customServiceIdBits, long workerId) {
        this.customServiceIdBits = customServiceIdBits;
        this.workerId = workerId;
        init();
    }
    
    @Override
    public synchronized long nextId() {
        long currentTimestamp = this.timeGen();
        // if currenttime is less, maybe some Exception
        if (currentTimestamp < this.lastTimestamp) {
            long offset = this.lastTimestamp - currentTimestamp;
            if (offset > 5L) {
                throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
            }
        
            try {
                this.wait(offset << 1);
                currentTimestamp = this.timeGen();
                if (currentTimestamp < this.lastTimestamp) {
                    throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
                }
            } catch (Exception var6) {
                throw new RuntimeException(var6);
            }
        }
        // concurrent
        if (this.lastTimestamp == currentTimestamp) {
            this.sequence = this.sequence + 1L & maxSequence;
            if (this.sequence == 0L) {
                currentTimestamp = this.waitUntilNextTime(this.lastTimestamp);
            }
        } else {
            this.sequence = ThreadLocalRandom.current().nextLong(1L, 3L);
        }
    
        this.lastTimestamp = currentTimestamp;
        return currentTimestamp - EPOCH << 22 | this.workerId << 12 | this.sequence << customServiceIdBits;
    }
    
    @Override
    public long currentId() {
        return this.currentId();
    }
    
    private void init() {
        if (this.workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException("work id must not be larger than " + MAX_WORKER_ID);
        }
        if (-1 == workerId) {
            // 根据本机ip 随机生成workerId
            InetAddress address;
            try {
                address = InetAddress.getLocalHost();
            } catch (final UnknownHostException e) {
                throw new IllegalStateException("Cannot get LocalHost InetAddress, please check your network!", e);
            }
            byte[] ipAddressByteArray = address.getAddress();
            this.workerId = (((ipAddressByteArray[ipAddressByteArray.length - 2] & 0B11) << Byte.SIZE) + (
                    ipAddressByteArray[ipAddressByteArray.length - 1] & 0xFF));
        }
        
        if (this.customServiceIdBits < 0 || this.customServiceIdBits > CUSTOM_SERVICE_BIT_MAX) {
            throw new IllegalArgumentException("customServiceIdBit id must not be larger than " + CUSTOM_SERVICE_BIT_MAX);
        }
        this.maxSequence = (long) Math.pow(2, SEQUENCE_BITS - customServiceIdBits) - 1;
    }
    
    protected long timeGen() {
        return System.currentTimeMillis();
    }
    
    /**
     * Block to the next millisecond until a new timestamp is obtained
     *
     * @param lastTimestamp The time intercept of the last ID generated
     * @return Current timestamp
     */
    private long waitUntilNextTime(long lastTimestamp) {
        long time;
        time = System.currentTimeMillis();
        while (time <= lastTimestamp) {
            time = System.currentTimeMillis();
        }
        
        return time;
    }
}
