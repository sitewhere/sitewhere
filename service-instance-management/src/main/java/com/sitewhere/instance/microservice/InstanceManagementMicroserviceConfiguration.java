package com.sitewhere.instance.microservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;

@Configuration
public class InstanceManagementMicroserviceConfiguration {

    @Bean
    public IInstanceManagementMicroservice instanceManagementMicroservice() {
	return new InstanceManagementMicroservice();
    }
}