/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.outbound.spi;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;

/**
 * Adds concept of filtering to outbound event processors.
 * 
 * @author Derek
 */
public interface IFilteredOutboundEventProcessor extends IOutboundEventProcessor {

    /**
     * Get the list of configured filters.
     * 
     * @return
     */
    public List<IDeviceEventFilter> getFilters();

    /**
     * Called if measurements data was not filtered.
     * 
     * @param context
     * @param measurements
     * @throws SiteWhereException
     */
    public void onMeasurementsNotFiltered(IDeviceEventContext context, IDeviceMeasurements measurements)
	    throws SiteWhereException;

    /**
     * Called if location data was not filtered.
     * 
     * @param context
     * @param location
     * @throws SiteWhereException
     */
    public void onLocationNotFiltered(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException;

    /**
     * Called if alert data was not filtered.
     * 
     * @param context
     * @param alert
     * @throws SiteWhereException
     */
    public void onAlertNotFiltered(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException;

    /**
     * Called if state change data was not filtered.
     * 
     * @param context
     * @param state
     * @throws SiteWhereException
     */
    public void onStateChangeNotFiltered(IDeviceEventContext context, IDeviceStateChange state)
	    throws SiteWhereException;

    /**
     * Called if command invocation data was not filtered.
     * 
     * @param context
     * @param invocation
     * @throws SiteWhereException
     */
    public void onCommandInvocationNotFiltered(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException;

    /**
     * Called if command response data was not filtered.
     * 
     * @param context
     * @param response
     * @throws SiteWhereException
     */
    public void onCommandResponseNotFiltered(IDeviceEventContext context, IDeviceCommandResponse response)
	    throws SiteWhereException;
}