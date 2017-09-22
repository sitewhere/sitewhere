package com.sitewhere.user.microservice;

import org.springframework.boot.SpringApplication;

import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.user.spi.microservice.IUserManagementMicroservice;

/**
 * Spring Boot application for tenant management microservice.
 * 
 * @author Derek
 */
public class UserManagementApplication extends MicroserviceApplication<IUserManagementMicroservice> {

    /** User management microservice */
    public static IUserManagementMicroservice SERVICE = new UserManagementMicroservice();

    /** Get microservice instance */
    public static IUserManagementMicroservice getUserManagementMicroservice() {
	return SERVICE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IUserManagementMicroservice getMicroservice() {
	return SERVICE;
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