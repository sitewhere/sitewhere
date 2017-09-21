package com.sitewhere.user.microservice;

import org.springframework.boot.SpringApplication;

import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.microservice.spi.MicroserviceNotAvailableException;
import com.sitewhere.user.spi.microservice.IUserManagementMicroservice;

/**
 * Spring Boot application for tenant management microservice.
 * 
 * @author Derek
 */
public class UserManagementApplication extends MicroserviceApplication<IUserManagementMicroservice> {

    /** User management microservice */
    public static IUserManagementMicroservice SERVICE = new UserManagementMicroservice();

    /** Get microservice instance after verifying it is available */
    public static IUserManagementMicroservice getUserManagementMicroservice() throws MicroserviceNotAvailableException {
	return assureAvailable(SERVICE);
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