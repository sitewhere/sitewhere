/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.spi.microservice;

import com.sitewhere.sources.spi.IEventSourcesManager;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Extends {@link IMicroserviceTenantEngine} with features specific to device
 * management.
 * 
 * @author Derek
 */
public interface IEventSourcesTenantEngine extends IMicroserviceTenantEngine {

    /**
     * Get event sources manager.
     * 
     * @return
     */
    public IEventSourcesManager getEventSourcesManager();
}