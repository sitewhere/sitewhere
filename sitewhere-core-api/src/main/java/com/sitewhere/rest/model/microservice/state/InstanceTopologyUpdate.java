/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.state;

import com.sitewhere.spi.microservice.state.IInstanceTopologyUpdate;
import com.sitewhere.spi.microservice.state.InstanceTopologyUpdateType;

/**
 * Model object for instance topology update.
 * 
 * @author Derek
 */
public class InstanceTopologyUpdate implements IInstanceTopologyUpdate {

    /** Microservice identifier */
    private String microserviceIdentifier;

    /** Microservice hostname */
    private String microserviceHostname;

    /** Topology update type */
    private InstanceTopologyUpdateType type;

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdate#
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
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdate#
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
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdate#getType()
     */
    @Override
    public InstanceTopologyUpdateType getType() {
	return type;
    }

    public void setType(InstanceTopologyUpdateType type) {
	this.type = type;
    }
}