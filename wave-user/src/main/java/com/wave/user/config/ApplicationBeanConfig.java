package com.wave.user.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import org.apache.shardingsphere.orchestration.config.OrchestrationConfiguration;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

@Configuration
public class ApplicationBeanConfig {

    @NacosInjected
    private ConfigService configService;

    @NacosInjected
    private NacosConfigProperties properties;

    public DataSource getDataSource() throws SQLException {
        // OrchestrationShardingDataSourceFactory 可替换成 OrchestrationMasterSlaveDataSourceFactory 或 OrchestrationEncryptDataSourceFactory
        return OrchestrationShardingDataSourceFactory.createDataSource(
                createDataSourceMap(), createShardingRuleConfig(), new HashMap<String, Object>(), new Properties(),
                new OrchestrationConfiguration(createCenterConfigurationMap()));
    }
    private Map<String, CenterConfiguration> createCenterConfigurationMap() {
        Map<String, CenterConfiguration> instanceConfigurationMap = new HashMap<String, CenterConfiguration>();
        CenterConfiguration config = createCenterConfiguration();
        instanceConfigurationMap.put("orchestration-shardingsphere-data-source", config);
        return instanceConfigurationMap;
    }
    private CenterConfiguration createCenterConfiguration() {
        Properties properties = new Properties();
        properties.setProperty("overwrite", "false");
        properties.setProperty("")
        CenterConfiguration result = new CenterConfiguration("nacos", properties);
        result.setServerLists("localhost:2181");
        result.setNamespace("shardingsphere-orchestration");
        result.setOrchestrationType("config_center");
        return result;
    }
}
