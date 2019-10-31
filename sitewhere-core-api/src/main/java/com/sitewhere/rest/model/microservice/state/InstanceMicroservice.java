/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.state;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.spi.microservice.state.IInstanceMicroservice;
import com.sitewhere.spi.microservice.state.IInstanceTenantEngine;
import com.sitewhere.spi.microservice.state.IMicroserviceState;

/**
 * Model object for a microservice represented in the instance topology.
 */
public class InstanceMicroservice implements IInstanceMicroservice {

    /** Latest state */
    private IMicroserviceState latestState;

    /** Map of tenant engines by tenant id */
    private Map<UUID, IInstanceTenantEngine> tenantEngines = new HashMap<UUID, IInstanceTenantEngine>();

    /** Time last update was received */
    private long lastUpdated;

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IInstanceMicroservice#getLatestState()
     */
    @Override
    public IMicroserviceState getLatestState() {
	return latestState;
    }

    public void setLatestState(IMicroserviceState latestState) {
	this.latestState = latestState;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IInstanceMicroservice#getTenantEngines()
     */
    @Override
    public Map<UUID, IInstanceTenantEngine> getTenantEngines() {
	return tenantEngines;
    }

    public void setTenantEngines(Map<UUID, IInstanceTenantEngine> tenantEngines) {
	this.tenantEngines = tenantEngines;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IInstanceMicroservice#getLastUpdated()
     */
    @Override
    public long getLastUpdated() {
	return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
	this.lastUpdated = lastUpdated;
    }
}