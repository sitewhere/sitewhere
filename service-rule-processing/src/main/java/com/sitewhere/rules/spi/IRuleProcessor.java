/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rules.spi;

import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Component that processes events based on a set of rules.
 */
public interface IRuleProcessor extends ITenantEngineLifecycleComponent {

    /**
     * Get unique id for processor.
     * 
     * @return
     */
    public String getProcessorId();

    /**
     * Get number of threads used for processing events.
     * 
     * @return
     */
    public int getNumProcessingThreads();

    /**
     * Responds to a measurement event.
     * 
     * @param context
     * @param measurement
     * @throws SiteWhereException
     */
    public void onMeasurement(IDeviceEventContext context, IDeviceMeasurement measurement) throws SiteWhereException;

    /**
     * Responds to a location event.
     * 
     * @param context
     * @param location
     * @throws SiteWhereException
     */
    public void onLocation(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException;

    /**
     * Responds to an alert event.
     * 
     * @param context
     * @param alert
     * @throws SiteWhereException
     */
    public void onAlert(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException;

    /**
     * Responds to a command invocation event.
     * 
     * @param context
     * @param invocation
     * @throws SiteWhereException
     */
    public void onCommandInvocation(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException;

    /**
     * Responds to a command response event.
     * 
     * @param context
     * @param response
     * @throws SiteWhereException
     */
    public void onCommandResponse(IDeviceEventContext context, IDeviceCommandResponse response)
	    throws SiteWhereException;

    /**
     * Responds to a state change event.
     * 
     * @param context
     * @param state
     * @throws SiteWhereException
     */
    public void onStateChange(IDeviceEventContext context, IDeviceStateChange state) throws SiteWhereException;

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