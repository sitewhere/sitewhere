/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.microservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}