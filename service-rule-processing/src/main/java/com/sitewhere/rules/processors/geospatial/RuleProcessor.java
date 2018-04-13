/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rules.processors.geospatial;

import com.sitewhere.rules.spi.IRuleProcessor;
import com.sitewhere.rules.spi.microservice.IRuleProcessingMicroservice;
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

/**
 * Default implementation of {@link IRuleProcessor} that acts as a common base
 * class for other rule processors.
 * 
 * @author Derek
 */
public class RuleProcessor extends TenantEngineLifecycleComponent implements IRuleProcessor {

    /** Default number of threads used for processing */
    private static final int DEFAULT_NUM_PROCESSING_THREADS = 2;

    /** Unqiue processor id */
    private String processorId;

    /** Number of threads used for processing events */
    private int numProcessingThreads = DEFAULT_NUM_PROCESSING_THREADS;

    /*
     * @see com.sitewhere.rules.spi.IRuleProcessor#onMeasurements(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurements)
     */
    @Override
    public void onMeasurements(IDeviceEventContext context, IDeviceMeasurements measurements)
	    throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.rules.spi.IRuleProcessor#onLocation(com.sitewhere.spi.device.
     * event.IDeviceEventContext, com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocation(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.rules.spi.IRuleProcessor#onAlert(com.sitewhere.spi.device.event
     * .IDeviceEventContext, com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlert(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.rules.spi.IRuleProcessor#onCommandInvocation(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void onCommandInvocation(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.rules.spi.IRuleProcessor#onCommandResponse(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandResponse)
     */
    @Override
    public void onCommandResponse(IDeviceEventContext context, IDeviceCommandResponse response)
	    throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.rules.spi.IRuleProcessor#onStateChange(com.sitewhere.spi.device
     * .event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceStateChange)
     */
    @Override
    public void onStateChange(IDeviceEventContext context, IDeviceStateChange state) throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.rules.spi.IRuleProcessor#getProcessorId()
     */
    @Override
    public String getProcessorId() {
	return processorId;
    }

    public void setProcessorId(String processorId) {
	this.processorId = processorId;
    }

    /*
     * @see com.sitewhere.rules.spi.IRuleProcessor#getNumProcessingThreads()
     */
    @Override
    public int getNumProcessingThreads() {
	return numProcessingThreads;
    }

    public void setNumProcessingThreads(int numProcessingThreads) {
	this.numProcessingThreads = numProcessingThreads;
    }

    /*
     * @see com.sitewhere.rules.spi.IRuleProcessor#getDeviceManagement()
     */
    @Override
    public IDeviceManagement getDeviceManagement() {
	return ((IRuleProcessingMicroservice) getTenantEngine().getMicroservice()).getDeviceManagementApiDemux()
		.getApiChannel();
    }

    /*
     * @see com.sitewhere.rules.spi.IRuleProcessor#getDeviceEventManagement()
     */
    @Override
    public IDeviceEventManagement getDeviceEventManagement() {
	return ((IRuleProcessingMicroservice) getTenantEngine().getMicroservice()).getDeviceEventManagementApiDemux()
		.getApiChannel();
    }
}