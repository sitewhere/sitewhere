/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.state;

import java.util.List;
import java.util.UUID;

import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Contains information about the current state of a microservice tenant engine.
 * 
 * @author Derek
 */
public interface ITenantEngineState {

    /**
     * Get microservice details.
     * 
     * @return
     */
    public IMicroserviceDetails getMicroservice();

    /**
     * Get tenant id.
     * 
     * @return
     */
    public UUID getTenantId();

    /**
     * Get current lifeycle status of tenant engine.
     * 
     * @return
     */
    public LifecycleStatus getLifecycleStatus();

    /**
     * If in an error state, returns the stack of error messages.
     * 
     * @return
     */
    public List<String> getLifecycleErrorStack();
}