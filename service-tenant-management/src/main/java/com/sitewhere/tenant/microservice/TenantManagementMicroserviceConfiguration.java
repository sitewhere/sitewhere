package com.sitewhere.tenant.microservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sitewhere.tenant.kafka.TenantBootstrapModelConsumer;
import com.sitewhere.tenant.kafka.TenantModelProducer;
import com.sitewhere.tenant.spi.kafka.ITenantBootstrapModelConsumer;
import com.sitewhere.tenant.spi.kafka.ITenantModelProducer;
import com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice;
import com.sitewhere.tenant.spi.templates.ITenantTemplateManager;
import com.sitewhere.tenant.templates.TenantTemplateManager;

@Configuration
public class TenantManagementMicroserviceConfiguration {

    @Bean
    public ITenantManagementMicroservice tenantManagementMicroservice() {
	return new TenantManagementMicroservice();
    }

    @Bean
    public ITenantTemplateManager tenantTemplateManager() {
	return new TenantTemplateManager();
    }

    @Bean
    public ITenantModelProducer tenantModelProducer() {
	return new TenantModelProducer();
    }

    @Bean
    public ITenantBootstrapModelConsumer tenantBootstrapModelConsumer() {
	return new TenantBootstrapModelConsumer();
    }
}