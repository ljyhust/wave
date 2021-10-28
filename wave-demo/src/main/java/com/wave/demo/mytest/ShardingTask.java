package com.wave.demo.mytest;

import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lijinyang
 * @date 2021/10/15 10:29
 */
@Component
public class ShardingTask {

    @Autowired
    ILoadBalancer loadBalancer;

    @Scheduled(cron = "0 0 */1 * * ?")
    public void execute() {

        Map<String, List> tenantDatas = new HashMap<>(30);

        // 分片参数
        List<Server> servers = loadBalancer.getReachableServers();
        String localHost = "";
        int serverIndex = 0;
        for (Server server : servers) {
            if (server.getHost().equals(localHost)) {
                break;
            }
            serverIndex++;
        }

        // 分任务
        Map<String, List> datas = new HashMap<>();
        for (Map.Entry<String, List> entry : tenantDatas.entrySet()) {
            if (entry.getKey().hashCode() % servers.size() == serverIndex) {
                datas.put(entry.getKey(), entry.getValue());
            }
        }
        run(datas);
    }

    private void run(Object param) {
        // .... 执行任务
    }
}
