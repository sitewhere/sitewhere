/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.ws.components.topology;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Captures event information for a topology change that involves a tenant
 * engine.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class TenantTopologyEvent implements ITenantEngineState {

    /** Event type */
    private TopologyEventType type;

    /** Microservice information */
    private IMicroserviceDetails microservice;

    /** Tenant id */
    private UUID tenantId;

    /** Status of tenant */
    private LifecycleStatus lifecycleStatus;

    /** Lifecycle error message stack */
    private List<String> lifecycleErrorStack;

    public TenantTopologyEvent(TopologyEventType type, ITenantEngineState state) {
	this.type = type;
	this.microservice = state.getMicroservice();
	this.tenantId = state.getTenantId();
	this.lifecycleStatus = state.getLifecycleStatus();
	this.lifecycleErrorStack = state.getLifecycleErrorStack();
    }

    public TopologyEventType getType() {
	return type;
    }

    public void setType(TopologyEventType type) {
	this.type = type;
    }

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