package com.wave.url.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class GracefulShutdown implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {
    
    private volatile Connector connector;
    
    @Override
    public void customize(Connector connector) {
        this.connector = connector;
    }
    
    
    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        // 停止当前连接
        log.error("Shutting down connect {} {} {} {}", connector.getPort(), connector.getRedirectPort(), connector.getStateName(), connector.getLocalPort());
        this.connector.pause();
        Executor executor = this.connector.getProtocolHandler().getExecutor();
        if (executor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) executor;
            poolExecutor.shutdown();
            try {
                if (!poolExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                    log.warn("Tomcat thread pool did not shut down gracefully within "
                            + "30 seconds. Proceeding with forceful shutdown");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
