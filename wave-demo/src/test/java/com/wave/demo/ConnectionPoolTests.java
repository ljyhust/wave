package com.wave.demo;

import com.alibaba.fastjson.JSON;
import com.wave.demo.concurrent.MysqlConnectionPool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ConnectionPoolTests{

    private String sql = "SELECT * FROM `user`";

    private String url = "jdbc:mysql://172.16.100.102:3306/test";
    private String userName = "eHRAdmin";
    private String password = "shangde_wings";
    @Test
    public void testConnectionPool() throws Exception {
        List<Connection> connPool = new LinkedList<>();
        String url = "jdbc:mysql://172.16.100.102:3306/sso";
        String userName = "eHRAdmin";
        String password = "shangde_wings";
        Class.forName("com.mysql.jdbc.Driver");

        for (int i = 0; i < 10; i++) {
            Connection connection = DriverManager.getConnection(url, userName, password);
            DatabaseMetaData metaData = connection.getMetaData();
            log.info("====> schema {}", metaData);
            connPool.add(connection);
        }

        Thread.sleep(60000 * 30);
    }

    @Test
    public void testExecuteConnection() throws Exception {
        testExecuteTest(3000, 2);
        testExecuteTest(3000, 10);
        testExecuteTest(3000, 20);
        testExecuteTest(3000, 50);
        testExecuteTest(3000, 100);
        testExecuteTest(3000, 200);
        testExecuteTest(3000, 300);
        testExecuteTest(3000, 400);
        testExecuteTest(3000, 500);
        testExecuteTest(3000, 600);
        testExecuteTest(3000, 700);
    }

    public ExecuteInfo testExecuteTest(int threadSize, int poolSize) throws Exception {
        MysqlConnectionPool connectionPool = new MysqlConnectionPool(url, userName, password);
        connectionPool.setPoolSize(poolSize);
        connectionPool.init();
        ExecuteInfo executeInfo = new ExecuteInfo(0, Integer.MAX_VALUE, 0);
        Long maxTime = 0L;
        Long minTime = (long)Integer.MAX_VALUE;
        CountDownLatch downLatch = new CountDownLatch(threadSize);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadSize, new Runnable() {
            @Override
            public void run() {
                log.info("=====> start connection pool test");
            }
        });
        for(int i = 0; i < threadSize; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 执行语句
                    Connection connection = null;
                    long s1 = 0L;
                    try {
                        cyclicBarrier.await();
                        s1 = System.currentTimeMillis();
                        connection = connectionPool.getConnection();
                        boolean execute = connection.prepareStatement(sql).execute();
                        //log.info("====> sql excute res is {}", execute);
                    } catch (Exception e) {
                        log.info("===> {}", e);
                    } finally {
                        if (null != connection) {
                            try {
                                connectionPool.returnConnection(connection);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    long s2 = System.currentTimeMillis();
                    long thisTime = s2 - s1;
                    if (thisTime > executeInfo.getMaxTime()) {
                        executeInfo.setMaxTime(thisTime);
                    }
                    if (thisTime < executeInfo.getMinTime()) {
                        executeInfo.setMinTime(thisTime);
                    }
                    executeInfo.setMeanTime(executeInfo.getMeanTime() + thisTime);
                    downLatch.countDown();
                }
            }).start();
        }
        downLatch.await();
        executeInfo.setMeanTime(executeInfo.getMeanTime() / threadSize);
        log.info("====> poolSize {} ; executeInfo {}", poolSize, executeInfo.toString());
        return executeInfo;
    }

    @Data
    class ExecuteInfo {
        private long maxTime;

        private long minTime;

        private long meanTime;

        public ExecuteInfo(long maxTime, long minTime, long meanTime) {
            this.maxTime = maxTime;
            this.minTime = minTime;
            this.meanTime = meanTime;
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
        }
    }
}
