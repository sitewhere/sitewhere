package com.sitewhere.tenant.microservice;

import org.springframework.boot.SpringApplication;

import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice;

/**
 * Spring Boot application for tenant management microservice.
 * 
 * @author Derek
 */
public class TenantManagementApplication extends MicroserviceApplication<ITenantManagementMicroservice> {

    /** Tenant management microservice */
    private static ITenantManagementMicroservice SERVICE = new TenantManagementMicroservice();

    /** Get microservice instance */
    public static ITenantManagementMicroservice getTenantManagementMicroservice() {
	return SERVICE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public ITenantManagementMicroservice getMicroservice() {
	return SERVICE;
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