package com.sitewhere.tenant.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;

import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice;

/**
 * Spring Boot application for tenant management microservice.
 * 
 * @author Derek
 */
public class TenantManagementApplication extends MicroserviceApplication<ITenantManagementMicroservice> {

    @Bean
    public ITenantManagementMicroservice tenantManagementMicroservice() {
	return new TenantManagementMicroservice();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public ITenantManagementMicroservice getMicroservice() {
	return tenantManagementMicroservice();
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