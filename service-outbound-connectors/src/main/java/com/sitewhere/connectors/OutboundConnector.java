/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors;

import com.sitewhere.connectors.spi.IOutboundConnector;
import com.sitewhere.connectors.spi.microservice.IOutboundConnectorsMicroservice;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Default implementation of {@link IOutboundConnector}.
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
     * @see com.sitewhere.connectors.spi.IOutboundConnector#getDeviceManagement()
     */
    @Override
    public IDeviceManagement getDeviceManagement() {
	return ((IOutboundConnectorsMicroservice) getTenantEngine().getMicroservice()).getDeviceManagement();
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IOutboundConnector#getDeviceEventManagement()
     */
    @Override
    public IDeviceEventManagement getDeviceEventManagement() {
	return ((IOutboundConnectorsMicroservice) getTenantEngine().getMicroservice())
		.getDeviceEventManagementApiChannel();
    }
}