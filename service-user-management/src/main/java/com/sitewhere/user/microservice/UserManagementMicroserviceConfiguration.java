package com.sitewhere.user.microservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sitewhere.user.spi.microservice.IUserManagementMicroservice;

/**
 * Spring bean configuration for microservice.
 * 
 * @author Derek
 */
@Configuration
public class UserManagementMicroserviceConfiguration {

    @Bean
    public IUserManagementMicroservice userManagementMicroservice() {
	return new UserManagementMicroservice();
    }
}