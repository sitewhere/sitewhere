/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.state;

import com.sitewhere.spi.microservice.state.IMicroserviceDetails;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Model object that represents current microservice state.
 * 
 * @author Derek
 */
public class MicroserviceState implements IMicroserviceState {

    /** Microservice details */
    private IMicroserviceDetails microserviceDetails;

    /** Lifecycle status */
    private LifecycleStatus lifecycleStatus;

    /*
     * @see com.sitewhere.spi.microservice.state.IMicroserviceState#
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
     * com.sitewhere.spi.microservice.state.IMicroserviceState#getLifecycleStatus()
     */
    @Override
    public LifecycleStatus getLifecycleStatus() {
	return lifecycleStatus;
    }

    public void setLifecycleStatus(LifecycleStatus lifecycleStatus) {
	this.lifecycleStatus = lifecycleStatus;
    }
}