package com.sitewhere.user.microservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.user.spi.microservice.IUserManagementMicroservice;

/**
 * Spring Boot application for tenant management microservice.
 * 
 * @author Derek
 */
@ComponentScan
public class UserManagementApplication extends MicroserviceApplication<IUserManagementMicroservice> {

    @Autowired
    private IUserManagementMicroservice microservice;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IUserManagementMicroservice getMicroservice() {
	return microservice;
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