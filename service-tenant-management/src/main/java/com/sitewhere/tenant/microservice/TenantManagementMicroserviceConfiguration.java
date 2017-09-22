package com.sitewhere.tenant.microservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice;

@Configuration
public class TenantManagementMicroserviceConfiguration {

    @Bean
    public ITenantManagementMicroservice tenantManagementMicroservice() {
	return new TenantManagementMicroservice();
    }
}