package com.wave.demo.concurrent;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * mysql数据库连接池demo
 */
@Slf4j
public class MysqlConnectionPool {

    private String url;

    private String userName;

    private String password;

    /**
     * 连接池大小，默认为8
     */
    private int poolSize = 8;

    private long waitTime = -1L;

    /**
     * 连接池维护队列
     */
    private BlockingQueue<Connection> idleQueue = new LinkedBlockingQueue<>();

    public MysqlConnectionPool(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    /**
     * 初始化连接池
     * @throws Exception
     */
    public void init() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        try {
            for (int i = 0; i < poolSize; i++) {
                Connection connection = DriverManager.getConnection(url, userName, password);
                DatabaseMetaData metaData = connection.getMetaData();
                //log.info("====> schema {}", metaData);
                idleQueue.add(connection);
            }
        }  catch (Exception e) {
            throw new RuntimeException("init connect pool error");
        }
    }

    public Connection getConnection() throws Exception {
        if (-1L == this.waitTime) {
            return idleQueue.take();
        }
        return idleQueue.poll(waitTime, TimeUnit.MILLISECONDS);
    }

    public Connection getConnection(long waitTime) throws Exception {
        return idleQueue.poll(waitTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 返回连接
     * @param connection
     * @throws Exception
     */
    public void returnConnection(Connection connection) throws Exception {
        idleQueue.put(connection);
    }

    /**
     * 设置连接池大小
     * @param size
     */
    public void setPoolSize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must large than 0");
        }
        this.poolSize = size;
    }
    /**
     * 设置获取连接等待时间
     * @param time 等待时间 millseconds
     */
    public void setWaitTime(long time) {
        if (time < 0) {
            throw new IllegalArgumentException("time must large than 0");
        }
        this.waitTime = time;
    }
}
