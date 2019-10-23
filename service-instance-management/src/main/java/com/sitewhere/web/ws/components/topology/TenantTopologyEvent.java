/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.ws.components.topology;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.microservice.state.ILifecycleComponentState;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;
import com.sitewhere.spi.microservice.state.ITenantEngineState;

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

    /** Component state tree */
    private ILifecycleComponentState componentState;

    public TenantTopologyEvent(TopologyEventType type, ITenantEngineState state) {
	this.type = type;
	this.microservice = state.getMicroservice();
	this.tenantId = state.getTenantId();
	this.componentState = state.getComponentState();
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
     * com.sitewhere.spi.microservice.state.ITenantEngineState#getComponentState()
     */
    @Override
    public ILifecycleComponentState getComponentState() {
	return componentState;
    }

    public void setComponentState(ILifecycleComponentState componentState) {
	this.componentState = componentState;
    }
}