/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.state;

import com.sitewhere.spi.microservice.state.IInstanceTopologyEntry;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;

/**
 * Entry for a microservice within the instance topology.
 * 
 * @author Derek
 */
public class InstanceTopologyEntry implements IInstanceTopologyEntry {

    /** Microservice details */
    private IMicroserviceDetails microserviceDetails;

    /** Last updated timestamp */
    private long lastUpdated;

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyEntry#
     * getMicroserviceDetails()
     */
    @Override
    public IMicroserviceDetails getMicroserviceDetails() {
	return microserviceDetails;
    }

    public void setMicroserviceDetails(IMicroserviceDetails microserviceDetails) {
	this.microserviceDetails = microserviceDetails;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IInstanceTopologyEntry#getLastUpdated()
     */
    @Override
    public long getLastUpdated() {
	return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
	this.lastUpdated = lastUpdated;
    }
}