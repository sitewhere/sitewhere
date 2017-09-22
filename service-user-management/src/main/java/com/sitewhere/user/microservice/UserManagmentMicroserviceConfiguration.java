package com.sitewhere.user.microservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sitewhere.user.spi.microservice.IUserManagementMicroservice;

@Configuration
public class UserManagmentMicroserviceConfiguration {

    @Bean
    public IUserManagementMicroservice userManagementMicroservice() {
	return new UserManagementMicroservice();
    }
}