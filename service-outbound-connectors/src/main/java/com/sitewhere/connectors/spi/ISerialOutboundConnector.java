/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.spi;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.kafka.IEnrichedEventPayload;

/**
 * Outbound connector that processes each batch event individually.
 * 
 * @author Derek
 */
public interface ISerialOutboundConnector extends IOutboundConnector {

    /**
     * Executes processing code for a device measurement event.
     * 
     * @param context
     * @param measurement
     * @throws SiteWhereException
     */
    public void onMeasurement(IDeviceEventContext context, IDeviceMeasurement measurement) throws SiteWhereException;

    /**
     * Executes processing code for a device location event.
     * 
     * @param context
     * @param location
     * @throws SiteWhereException
     */
    public void onLocation(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException;

    /**
     * Executes processing code for a device alert event.
     * 
     * @param context
     * @param alert
     * @throws SiteWhereException
     */
    public void onAlert(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException;

    /**
     * Executes processing code for a device command invocation event.
     * 
     * @param context
     * @param invocation
     * @throws SiteWhereException
     */
    public void onCommandInvocation(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException;

    /**
     * Executes processing code for a device command response event.
     * 
     * @param context
     * @param response
     * @throws SiteWhereException
     */
    public void onCommandResponse(IDeviceEventContext context, IDeviceCommandResponse response)
	    throws SiteWhereException;

    /**
     * Executes processing code for a device state change event.
     * 
     * @param context
     * @param state
     * @throws SiteWhereException
     */
    public void onStateChange(IDeviceEventContext context, IDeviceStateChange state) throws SiteWhereException;

    /**
     * Handle a single failed record.
     * 
     * @param payload
     * @throws SiteWhereException
     */
    public void handleFailedRecord(IEnrichedEventPayload payload) throws SiteWhereException;
}