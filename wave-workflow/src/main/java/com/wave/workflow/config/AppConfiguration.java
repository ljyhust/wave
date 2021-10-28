package com.wave.workflow.config;

import com.zaxxer.hikari.HikariDataSource;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.spring.ProcessEngineFactoryBean;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * @author lijinyang
 * @date 2021/10/22 16:44
 */
@Configuration
public class AppConfiguration {

    @Bean("dataSourceCamunda")
    @ConfigurationProperties(prefix = "spring.datasource.hikari.camunda")
    public DataSource dataSourceWorkflow() {
        // Use a JNDI data source or read the properties from
        // env or a properties file.
        // Note: The following shows only a simple data source
        // for In-Memory H2 database.
        return new HikariDataSource();
    }

    @Bean
    public PlatformTransactionManager transactionManagerWorkflow() {
        return new DataSourceTransactionManager(dataSourceWorkflow());
    }

    @Bean
    public SpringProcessEngineConfiguration processEngineConfiguration() {
        SpringProcessEngineConfiguration config = new SpringProcessEngineConfiguration();

        config.setDataSource(dataSourceWorkflow());
        config.setTransactionManager(transactionManagerWorkflow());

        config.setDatabaseSchemaUpdate("true");
        config.setHistory("audit");
        config.setJobExecutorActivate(true);

        return config;
    }

    @Bean
    public ProcessEngineFactoryBean processEngine() {
        ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
        factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
        return factoryBean;
    }

    @Bean
    public RepositoryService repositoryService(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    @Bean
    public RuntimeService runtimeService(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    @Bean
    public TaskService taskService(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }
}
