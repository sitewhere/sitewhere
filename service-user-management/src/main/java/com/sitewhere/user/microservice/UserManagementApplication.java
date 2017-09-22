package com.sitewhere.user.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.user.spi.microservice.IUserManagementMicroservice;

/**
 * Spring Boot application for tenant management microservice.
 * 
 * @author Derek
 */
@Configuration
@ComponentScan
public class UserManagementApplication extends MicroserviceApplication<IUserManagementMicroservice> {

    @Bean
    public IUserManagementMicroservice userManagementMicroservice() {
	return new UserManagementMicroservice();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IUserManagementMicroservice getMicroservice() {
	return userManagementMicroservice();
    }

    /**
     * Entry point for Spring Boot.
     * 
     * @param args
     */
    public static void main(String[] args) {
	SpringApplication.run(UserManagementApplication.class, args);
    }
}