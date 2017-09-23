package com.sitewhere.microservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sitewhere.microservice.configuration.ZookeeperManager;
import com.sitewhere.microservice.security.TokenManagement;
import com.sitewhere.microservice.spi.configuration.IZookeeperManager;
import com.sitewhere.microservice.spi.security.ITokenManagement;

@Configuration
public class MicroserviceConfiguration {

    @Bean
    public IZookeeperManager zookeeperManager() {
	return new ZookeeperManager();
    }

    @Bean
    public ITokenManagement tokenManagement() {
	return new TokenManagement();
    }
}
