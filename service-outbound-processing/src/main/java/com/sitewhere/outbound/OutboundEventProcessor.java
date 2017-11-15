/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.outbound;

import com.sitewhere.outbound.spi.IOutboundEventProcessor;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
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
 * Default implementation of {@link IOutboundEventProcessor}.
 * 
 * @author Derek
 */
public abstract class OutboundEventProcessor extends TenantLifecycleComponent implements IOutboundEventProcessor {

    /** Default number of threads used for processing */
    private static final int DEFAULT_NUM_PROCESSING_THREADS = 2;

    /** Unqiue processor id */
    private String processorId;

    /** Number of threads used for processing events */
    private int numProcessingThreads = DEFAULT_NUM_PROCESSING_THREADS;

    /** Handle to device management implementation */
    private IDeviceManagement deviceManagement;

    /** Handle to device event management implementation */
    private IDeviceEventManagement deviceEventManagement;

    public OutboundEventProcessor() {
	super(LifecycleComponentType.OutboundEventProcessor);
    }

    /*
     * @see com.sitewhere.outbound.spi.IOutboundEventProcessor#onMeasurements(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurements)
     */
    @Override
    public void onMeasurements(IDeviceEventContext context, IDeviceMeasurements measurements)
	    throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.outbound.spi.IOutboundEventProcessor#onLocation(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocation(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.outbound.spi.IOutboundEventProcessor#onAlert(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlert(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.outbound.spi.IOutboundEventProcessor#onCommandInvocation(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void onCommandInvocation(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.outbound.spi.IOutboundEventProcessor#onCommandResponse(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandResponse)
     */
    @Override
    public void onCommandResponse(IDeviceEventContext context, IDeviceCommandResponse response)
	    throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.outbound.spi.IOutboundEventProcessor#onStateChange(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceStateChange)
     */
    @Override
    public void onStateChange(IDeviceEventContext context, IDeviceStateChange state) throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.outbound.spi.IOutboundEventProcessor#getProcessorId()
     */
    @Override
    public String getProcessorId() {
	return processorId;
    }

    public void setProcessorId(String processorId) {
	this.processorId = processorId;
    }

    /*
     * @see
     * com.sitewhere.outbound.spi.IOutboundEventProcessor#getNumProcessingThreads()
     */
    @Override
    public int getNumProcessingThreads() {
	return numProcessingThreads;
    }

    public void setNumProcessingThreads(int numProcessingThreads) {
	this.numProcessingThreads = numProcessingThreads;
    }

    /*
     * @see com.sitewhere.outbound.spi.IOutboundEventProcessor#getDeviceManagement()
     */
    @Override
    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    /*
     * @see
     * com.sitewhere.outbound.spi.IOutboundEventProcessor#setDeviceManagement(com.
     * sitewhere.spi.device.IDeviceManagement)
     */
    @Override
    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    /*
     * @see
     * com.sitewhere.outbound.spi.IOutboundEventProcessor#getDeviceEventManagement()
     */
    @Override
    public IDeviceEventManagement getDeviceEventManagement() {
	return deviceEventManagement;
    }

    /*
     * @see
     * com.sitewhere.outbound.spi.IOutboundEventProcessor#setDeviceEventManagement(
     * com.sitewhere.spi.device.event.IDeviceEventManagement)
     */
    @Override
    public void setDeviceEventManagement(IDeviceEventManagement deviceEventManagement) {
	this.deviceEventManagement = deviceEventManagement;
    }
}