/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.state;

/**
 * Details about a tenant engine running within a SiteWhere instance.
 */
public interface IInstanceTenantEngine {

    /**
     * Get most recent tenant engine state.
     * 
     * @return
     */
    public ITenantEngineState getLatestState();

    /**
     * Get time the last microservice update was received.
     * 
     * @return
     */
    public long getLastUpdated();
}