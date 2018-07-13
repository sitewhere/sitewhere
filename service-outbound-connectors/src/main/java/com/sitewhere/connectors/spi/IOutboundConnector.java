/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.spi;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.microservice.kafka.payload.IEnrichedEventPayload;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Connects processed events to an external entity for further processing.
 * 
 * @author Derek
 */
public interface IOutboundConnector extends ITenantEngineLifecycleComponent {

    /**
     * Get unique id for connector.
     * 
     * @return
     */
    public String getConnectorId();

    /**
     * Get number of threads used for processing events.
     * 
     * @return
     */
    public int getNumProcessingThreads();

    /**
     * Process a batch of events.
     * 
     * @param payloads
     * @throws SiteWhereException
     */
    public void processEventBatch(List<IEnrichedEventPayload> payloads) throws SiteWhereException;

    /**
     * Handle a batch of events that could not be processed.
     * 
     * @param payloads
     * @param failReason
     * @throws SiteWhereException
     */
    public void handleFailedBatch(List<IEnrichedEventPayload> payloads, Throwable failReason) throws SiteWhereException;

    /**
     * Get device management API.
     * 
     * @return
     */
    public IDeviceManagement getDeviceManagement();

    /**
     * Get device event management API.
     * 
     * @return
     */
    public IDeviceEventManagement getDeviceEventManagement();
}