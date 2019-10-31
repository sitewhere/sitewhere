/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.state;

import java.util.Map;
import java.util.UUID;

/**
 * Microservice associated with a SiteWhere instance.
 */
public interface IInstanceMicroservice {

    /**
     * Get most recent microservice state.
     * 
     * @return
     */
    public IMicroserviceState getLatestState();

    /**
     * Get map of tenant engines indexed by tenant id.
     * 
     * @return
     */
    public Map<UUID, IInstanceTenantEngine> getTenantEngines();

    /**
     * Get time the last microservice update was received.
     * 
     * @return
     */
    public long getLastUpdated();
}