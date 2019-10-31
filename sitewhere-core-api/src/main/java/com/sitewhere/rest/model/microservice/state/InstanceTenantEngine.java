/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.state;

import com.sitewhere.spi.microservice.state.IInstanceTenantEngine;
import com.sitewhere.spi.microservice.state.ITenantEngineState;

/**
 * Model object for a tenant engine represented in the instance topology.
 */
public class InstanceTenantEngine implements IInstanceTenantEngine {

    /** Latest state */
    private ITenantEngineState latestState;

    /** Time last update was received */
    private long lastUpdated;

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IInstanceTenantEngine#getLatestState()
     */
    @Override
    public ITenantEngineState getLatestState() {
	return latestState;
    }

    public void setLatestState(ITenantEngineState latestState) {
	this.latestState = latestState;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IInstanceTenantEngine#getLastUpdated()
     */
    @Override
    public long getLastUpdated() {
	return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
	this.lastUpdated = lastUpdated;
    }
}