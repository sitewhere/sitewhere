/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.ws.components.topology;

import com.sitewhere.spi.microservice.state.IMicroserviceDetails;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Captures event information for a topology change that involves a
 * microservice.
 * 
 * @author Derek
 */
public class MicroserviceTopologyEvent {

    /** Event type */
    private TopologyEventType type;

    /** Microservice information */
    private IMicroserviceDetails microservice;

    /** Status of microservice */
    private LifecycleStatus status;

    public MicroserviceTopologyEvent(TopologyEventType type, IMicroserviceState state) {
	this.type = type;
	this.microservice = state.getMicroservice();
	this.status = state.getLifecycleStatus();
    }

    public TopologyEventType getType() {
	return type;
    }

    public void setType(TopologyEventType type) {
	this.type = type;
    }

    public IMicroserviceDetails getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroserviceDetails microservice) {
	this.microservice = microservice;
    }

    public LifecycleStatus getStatus() {
	return status;
    }

    public void setStatus(LifecycleStatus status) {
	this.status = status;
    }
}