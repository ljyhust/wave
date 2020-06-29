package com.wave.demo;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class ConnectionPoolTests{

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
}
