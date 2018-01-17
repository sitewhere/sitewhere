/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.ws.components.topology;

import com.sitewhere.spi.microservice.state.IMicroserviceDetails;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Captures event information for a topology change that involves a tenant
 * engine.
 * 
 * @author Derek
 */
public class TenantTopologyEvent implements ITenantEngineState {

    /** Event type */
    private TopologyEventType type;

    /** Microservice information */
    private IMicroserviceDetails microservice;

    /** Tenant id */
    private String tenantId;

    /** Status of tenant */
    private LifecycleStatus lifecycleStatus;

    public TenantTopologyEvent(TopologyEventType type, ITenantEngineState state) {
	this.type = type;
	this.microservice = state.getMicroservice();
	this.tenantId = state.getTenantId();
	this.lifecycleStatus = state.getLifecycleStatus();
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
    public String getTenantId() {
	return tenantId;
    }

    public void setTenantId(String tenantId) {
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
}