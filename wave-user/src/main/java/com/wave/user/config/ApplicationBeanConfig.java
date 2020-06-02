package com.wave.user.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import org.apache.shardingsphere.orchestration.center.config.CenterConfiguration;
import org.apache.shardingsphere.orchestration.center.config.OrchestrationConfiguration;
import org.apache.shardingsphere.shardingjdbc.orchestration.api.OrchestrationShardingDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class ApplicationBeanConfig {


}
