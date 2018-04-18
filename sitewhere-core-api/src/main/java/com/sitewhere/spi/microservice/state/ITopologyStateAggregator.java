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
import java.util.concurrent.TimeUnit;

import com.sitewhere.spi.SiteWhereException;

/**
 * Kafka consumer that listens for state updates and aggregates them to produce
 * an estimated topology of the SiteWhere instance.
 * 
 * @author Derek
 */
public interface ITopologyStateAggregator extends IMicroserviceStateUpdatesKafkaConsumer {

    /**
     * Get latest instance topology snapshot.
     * 
     * @return
     */
    public IInstanceTopologySnapshot getInstanceTopologySnapshot();

    /**
     * For a given type of microservice, find tenant engines across all instances
     * for the given tenant and return their state.
     * 
     * @param identifier
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    public List<ITenantEngineState> getTenantEngineState(String identifier, UUID tenantId) throws SiteWhereException;

    /**
     * Wait for a tenant engine of the given type for the given tenant id to become
     * available.
     * 
     * @param identifier
     * @param tenantId
     * @param duration
     * @param unit
     * @param logMessageDelay
     * @throws SiteWhereException
     */
    public void waitForTenantEngineAvailable(String identifier, UUID tenantId, long duration, TimeUnit unit,
	    long logMessageDelay) throws SiteWhereException;

    /**
     * Add listener for instance topology updates.
     * 
     * @param listener
     */
    public void addInstanceTopologyUpdatesListener(IInstanceTopologyUpdatesListener listener);

    /**
     * Remove listener for instance topology updates.
     * 
     * @param listener
     */
    public void removeInstanceTopologyUpdatesListener(IInstanceTopologyUpdatesListener listener);
}