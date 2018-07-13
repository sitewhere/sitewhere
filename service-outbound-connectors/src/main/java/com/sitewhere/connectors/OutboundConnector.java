/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors;

import java.util.List;

import com.sitewhere.connectors.spi.IOutboundConnector;
import com.sitewhere.connectors.spi.microservice.IOutboundConnectorsMicroservice;
import com.sitewhere.grpc.client.event.BlockingDeviceEventManagement;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.microservice.kafka.payload.IEnrichedEventPayload;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Default implementation of {@link IOutboundConnector}.
 * 
 * @author Derek
 */
public abstract class OutboundConnector extends TenantEngineLifecycleComponent implements IOutboundConnector {

    /** Default number of threads used for processing */
    private static final int DEFAULT_NUM_PROCESSING_THREADS = 2;

    /** Unqiue connector id */
    private String connectorId;

    /** Number of threads used for processing events */
    private int numProcessingThreads = DEFAULT_NUM_PROCESSING_THREADS;

    public OutboundConnector() {
	super(LifecycleComponentType.OutboundConnector);
    }

    /*
     * @see com.sitewhere.connectors.spi.IOutboundConnector#getConnectorId()
     */
    @Override
    public String getConnectorId() {
	return connectorId;
    }

    public void setConnectorId(String connectorId) {
	this.connectorId = connectorId;
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IOutboundConnector#getNumProcessingThreads()
     */
    @Override
    public int getNumProcessingThreads() {
	return numProcessingThreads;
    }

    public void setNumProcessingThreads(int numProcessingThreads) {
	this.numProcessingThreads = numProcessingThreads;
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IOutboundConnector#handleFailedBatch(java.util.
     * List, java.lang.Throwable)
     */
    @Override
    public void handleFailedBatch(List<IEnrichedEventPayload> payloads, Throwable failReason)
	    throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.connectors.spi.IOutboundConnector#getDeviceManagement()
     */
    @Override
    public IDeviceManagement getDeviceManagement() {
	return ((IOutboundConnectorsMicroservice) getTenantEngine().getMicroservice()).getDeviceManagementApiDemux()
		.getApiChannel();
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IOutboundConnector#getDeviceEventManagement()
     */
    @Override
    public IDeviceEventManagement getDeviceEventManagement() {
	return new BlockingDeviceEventManagement(((IOutboundConnectorsMicroservice) getTenantEngine().getMicroservice())
		.getDeviceEventManagementApiDemux().getApiChannel());
    }
}