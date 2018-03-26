/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.state;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Model object for tenant engine state.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class TenantEngineState implements ITenantEngineState {

    /** Microservice details */
    private IMicroserviceDetails microservice;

    /** Tenant id */
    private UUID tenantId;

    /** Lifecycle status */
    private LifecycleStatus lifecycleStatus;

    /** Lifecycle error message stack */
    private List<String> lifecycleErrorStack;

    /*
     * @see
     * com.sitewhere.spi.microservice.state.ITenantEngineState#getMicroservice()
     */
    @Override
    public IMicroserviceDetails getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroserviceDetails microservice) {
	this.microservice = microservice;
    }

    /*
     * @see com.sitewhere.spi.microservice.state.ITenantEngineState#getTenantId()
     */
    @Override
    public UUID getTenantId() {
	return tenantId;
    }

    public void setTenantId(UUID tenantId) {
	this.tenantId = tenantId;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.ITenantEngineState#getLifecycleStatus()
     */
    @Override
    public LifecycleStatus getLifecycleStatus() {
	return lifecycleStatus;
    }

    public void setLifecycleStatus(LifecycleStatus lifecycleStatus) {
	this.lifecycleStatus = lifecycleStatus;
    }

    /*
     * @see com.sitewhere.spi.microservice.state.ITenantEngineState#
     * getLifecycleErrorStack()
     */
    @Override
    public List<String> getLifecycleErrorStack() {
	return lifecycleErrorStack;
    }

    public void setLifecycleErrorStack(List<String> lifecycleErrorStack) {
	this.lifecycleErrorStack = lifecycleErrorStack;
    }
}