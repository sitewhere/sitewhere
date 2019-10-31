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
 */
public class MicroserviceState implements IMicroserviceState {

    /** Microservice details */
    private IMicroserviceDetails microservice;

    /** Lifecycle status */
    private LifecycleStatus lifecycleStatus;

    /** Milliseconds since service was started */
    private long uptime;

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IMicroserviceState#getMicroservice()
     */
    @Override
    public IMicroserviceDetails getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroserviceDetails microservice) {
	this.microservice = microservice;
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

    /*
     * @see com.sitewhere.spi.microservice.state.IMicroserviceState#getUptime()
     */
    @Override
    public long getUptime() {
	return uptime;
    }

    public void setUptime(long uptime) {
	this.uptime = uptime;
    }
}