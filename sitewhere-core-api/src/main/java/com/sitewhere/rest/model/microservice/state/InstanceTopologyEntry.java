/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.state;

import com.sitewhere.spi.microservice.state.IInstanceTopologyEntry;

/**
 * Entry for a microservice within the instance topology.
 * 
 * @author Derek
 */
public class InstanceTopologyEntry implements IInstanceTopologyEntry {

    /** Microservice identifier */
    private String microserviceIdentifier;

    /** Microservice hostname */
    private String microserviceHostname;

    /** Last updated timestamp */
    private long lastUpdated;

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyEntry#
     * getMicroserviceIdentifier()
     */
    @Override
    public String getMicroserviceIdentifier() {
	return microserviceIdentifier;
    }

    public void setMicroserviceIdentifier(String microserviceIdentifier) {
	this.microserviceIdentifier = microserviceIdentifier;
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyEntry#
     * getMicroserviceHostname()
     */
    @Override
    public String getMicroserviceHostname() {
	return microserviceHostname;
    }

    public void setMicroserviceHostname(String microserviceHostname) {
	this.microserviceHostname = microserviceHostname;
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