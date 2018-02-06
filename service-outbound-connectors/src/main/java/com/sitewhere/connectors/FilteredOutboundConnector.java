/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.connectors.spi.IDeviceEventFilter;
import com.sitewhere.connectors.spi.IFilteredOutboundConnector;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Extends {@link OutboundConnector} with filtering functionality.
 * 
 * @author Derek
 */
public abstract class FilteredOutboundConnector extends OutboundConnector implements IFilteredOutboundConnector {

    /** List of filters in order they should be applied */
    private List<IDeviceEventFilter> filters = new ArrayList<IDeviceEventFilter>();

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getLifecycleComponents().clear();
	for (IDeviceEventFilter filter : filters) {
	    startNestedComponent(filter, monitor, true);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	for (IDeviceEventFilter filter : filters) {
	    filter.stop(monitor);
	}
    }

    /*
     * @see
     * com.sitewhere.connectors.OutboundConnector#onMeasurements(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurements)
     */
    @Override
    public final void onMeasurements(IDeviceEventContext context, IDeviceMeasurements measurements)
	    throws SiteWhereException {
	if (!isFiltered(context, measurements)) {
	    onMeasurementsNotFiltered(context, measurements);
	}
    }

    /*
     * @see com.sitewhere.connectors.spi.IFilteredOutboundConnector#
     * onMeasurementsNotFiltered(com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurements)
     */
    @Override
    public void onMeasurementsNotFiltered(IDeviceEventContext context, IDeviceMeasurements measurements)
	    throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.connectors.OutboundConnector#onLocation(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public final void onLocation(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
	if (!isFiltered(context, location)) {
	    onLocationNotFiltered(context, location);
	}
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IFilteredOutboundConnector#onLocationNotFiltered
     * (com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocationNotFiltered(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.connectors.OutboundConnector#onAlert(com.sitewhere.spi.device.
     * event.IDeviceEventContext, com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public final void onAlert(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
	if (!isFiltered(context, alert)) {
	    onAlertNotFiltered(context, alert);
	}
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IFilteredOutboundConnector#onAlertNotFiltered(
     * com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlertNotFiltered(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.connectors.OutboundConnector#onStateChange(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceStateChange)
     */
    @Override
    public final void onStateChange(IDeviceEventContext context, IDeviceStateChange state) throws SiteWhereException {
	if (!isFiltered(context, state)) {
	    onStateChangeNotFiltered(context, state);
	}
    }

    /*
     * @see com.sitewhere.connectors.spi.IFilteredOutboundConnector#
     * onStateChangeNotFiltered(com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceStateChange)
     */
    @Override
    public void onStateChangeNotFiltered(IDeviceEventContext context, IDeviceStateChange state)
	    throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.connectors.OutboundConnector#onCommandInvocation(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public final void onCommandInvocation(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException {
	if (!isFiltered(context, invocation)) {
	    onCommandInvocationNotFiltered(context, invocation);
	}
    }

    /*
     * @see com.sitewhere.connectors.spi.IFilteredOutboundConnector#
     * onCommandInvocationNotFiltered(com.sitewhere.spi.device.event.
     * IDeviceEventContext, com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void onCommandInvocationNotFiltered(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.connectors.OutboundConnector#onCommandResponse(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandResponse)
     */
    @Override
    public final void onCommandResponse(IDeviceEventContext context, IDeviceCommandResponse response)
	    throws SiteWhereException {
	if (!isFiltered(context, response)) {
	    onCommandResponseNotFiltered(context, response);
	}
    }

    /*
     * @see com.sitewhere.connectors.spi.IFilteredOutboundConnector#
     * onCommandResponseNotFiltered(com.sitewhere.spi.device.event.
     * IDeviceEventContext, com.sitewhere.spi.device.event.IDeviceCommandResponse)
     */
    @Override
    public void onCommandResponseNotFiltered(IDeviceEventContext context, IDeviceCommandResponse response)
	    throws SiteWhereException {
    }

    /**
     * Indicates if an event is filtered.
     * 
     * @param context
     * @param event
     * @return
     * @throws SiteWhereException
     */
    protected boolean isFiltered(IDeviceEventContext context, IDeviceEvent event) throws SiteWhereException {
	for (IDeviceEventFilter filter : filters) {
	    if (filter.isFiltered(context, event)) {
		return true;
	    }
	}
	return false;
    }

    /*
     * @see com.sitewhere.connectors.spi.IFilteredOutboundConnector#getFilters()
     */
    @Override
    public List<IDeviceEventFilter> getFilters() {
	return filters;
    }

    public void setFilters(List<IDeviceEventFilter> filters) {
	this.filters = filters;
    }
}