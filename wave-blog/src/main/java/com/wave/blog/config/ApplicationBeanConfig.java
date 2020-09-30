package com.wave.blog.config;

import com.wave.common.WaveConstants;
import com.wave.consistency.id.DefaultServiceIdGenerator;
import com.wave.consistency.id.IdGeneratorManager;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBeanConfig implements ApplicationListener {
    
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        // TODO bits = 4L 可配置化
        IdGeneratorManager.registerGenerator(WaveBlogConstants.WAVE_BLOG_NAME, new DefaultServiceIdGenerator(4L));
    }
}
