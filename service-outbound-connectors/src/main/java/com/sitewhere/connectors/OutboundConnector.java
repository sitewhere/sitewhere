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
import com.sitewhere.grpc.client.event.BlockingDeviceEventManagement;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
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
     * @see
     * com.sitewhere.connectors.spi.IOutboundConnector#onMeasurements(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurements)
     */
    @Override
    public void onMeasurements(IDeviceEventContext context, IDeviceMeasurements measurements)
	    throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IOutboundConnector#onLocation(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocation(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IOutboundConnector#onAlert(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlert(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.connectors.spi.IOutboundConnector#onCommandInvocation(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void onCommandInvocation(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.connectors.spi.IOutboundConnector#onCommandResponse(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandResponse)
     */
    @Override
    public void onCommandResponse(IDeviceEventContext context, IDeviceCommandResponse response)
	    throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IOutboundConnector#onStateChange(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceStateChange)
     */
    @Override
    public void onStateChange(IDeviceEventContext context, IDeviceStateChange state) throws SiteWhereException {
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