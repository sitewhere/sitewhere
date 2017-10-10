package com.sitewhere.device.microservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice;

/**
 * Spring bean configuration for microservice.
 * 
 * @author Derek
 */
@Configuration
public class DeviceManagementMicroserviceConfiguration {

    @Bean
    public IDeviceManagementMicroservice deviceManagementMicroservice() {
	return new DeviceManagementMicroservice();
    }
}