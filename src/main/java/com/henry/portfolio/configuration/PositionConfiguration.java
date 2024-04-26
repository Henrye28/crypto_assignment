package com.henry.portfolio.configuration;

import com.henry.portfolio.position.PositionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class PositionConfiguration {

    @Bean
    public PositionService positionService(ResourceLoader resourceLoader){
        return new PositionService(resourceLoader);
    }

}

