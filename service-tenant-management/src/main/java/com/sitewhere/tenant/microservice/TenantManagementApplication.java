package com.sitewhere.tenant.microservice;

import org.springframework.boot.SpringApplication;

import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.microservice.spi.IMicroservice;

/**
 * Spring Boot application for tenant management microservice.
 * 
 * @author Derek
 */
public class TenantManagementApplication extends MicroserviceApplication {

    /** Tenant management microservice */
    private TenantManagement service = new TenantManagement();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IMicroservice getMicroservice() {
	return service;
    }

    /**
     * Entry point for Spring Boot.
     * 
     * @param args
     */
    public static void main(String[] args) {
	SpringApplication.run(TenantManagementApplication.class, args);
    }
}
