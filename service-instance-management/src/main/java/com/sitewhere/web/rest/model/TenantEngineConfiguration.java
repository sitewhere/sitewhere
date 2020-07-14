/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.microservice.configuration.model.instance.InstanceConfiguration;
import com.sitewhere.rest.model.microservice.MicroserviceSummary;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Wraps configuration of instance and tenant engine.
 */
public class TenantEngineConfiguration {

    /** Associated tenant information */
    private ITenant tenant;

    /** Associated microservice information */
    private MicroserviceSummary microservice;

    /** Instance configuration */
    private InstanceConfiguration instanceConfiguration;

    /** Microservice configuration */
    private JsonNode microserviceConfiguration;

    /** Tenant configuration */
    private JsonNode tenantConfiguration;

    public ITenant getTenant() {
	return tenant;
    }

    public void setTenant(ITenant tenant) {
	this.tenant = tenant;
    }

    public MicroserviceSummary getMicroservice() {
	return microservice;
    }

    public void setMicroservice(MicroserviceSummary microservice) {
	this.microservice = microservice;
    }

    public InstanceConfiguration getInstanceConfiguration() {
	return instanceConfiguration;
    }

    public void setInstanceConfiguration(InstanceConfiguration instanceConfiguration) {
	this.instanceConfiguration = instanceConfiguration;
    }

    public JsonNode getMicroserviceConfiguration() {
	return microserviceConfiguration;
    }

    public void setMicroserviceConfiguration(JsonNode microserviceConfiguration) {
	this.microserviceConfiguration = microserviceConfiguration;
    }

    public JsonNode getTenantConfiguration() {
	return tenantConfiguration;
    }

    public void setTenantConfiguration(JsonNode tenantConfiguration) {
	this.tenantConfiguration = tenantConfiguration;
    }
}
