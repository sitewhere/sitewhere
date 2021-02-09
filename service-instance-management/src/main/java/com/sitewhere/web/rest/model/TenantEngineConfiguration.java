/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
