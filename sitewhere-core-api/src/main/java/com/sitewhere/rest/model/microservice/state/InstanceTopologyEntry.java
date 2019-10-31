/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sitewhere.spi.microservice.state.IInstanceMicroservice;
import com.sitewhere.spi.microservice.state.IInstanceTopologyEntry;

/**
 * Entry for a microservice within the instance topology.
 */
public class InstanceTopologyEntry implements IInstanceTopologyEntry {

    /** Microservices by hostname */
    private Map<String, IInstanceMicroservice> microservicesByHostname = new ConcurrentHashMap<>();

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyEntry#
     * getMicroservicesByHostname()
     */
    @Override
    public Map<String, IInstanceMicroservice> getMicroservicesByHostname() {
	return microservicesByHostname;
    }

    public void setMicroservicesByHostname(Map<String, IInstanceMicroservice> microservicesByHostname) {
	this.microservicesByHostname = microservicesByHostname;
    }
}