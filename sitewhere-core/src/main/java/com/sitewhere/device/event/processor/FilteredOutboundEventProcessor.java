/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.event.processor;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.SiteWhere;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.processor.IDeviceEventFilter;
import com.sitewhere.spi.device.event.processor.IFilteredOutboundEventProcessor;

/**
 * Extends {@link OutboundEventProcessor} with filtering functionality.
 * 
 * @author Derek
 */
public abstract class FilteredOutboundEventProcessor extends OutboundEventProcessor
	implements IFilteredOutboundEventProcessor {

    /** List of filters in order they should be applied */
    private List<IDeviceEventFilter> filters = new ArrayList<IDeviceEventFilter>();

    /** Device management implementation */
    private IDeviceManagement deviceManagement;

    /** Event management implementation */
    private IDeviceEventManagement eventManagement;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
     */
    @Override
    public void start() throws SiteWhereException {
	this.deviceManagement = SiteWhere.getServer().getDeviceManagement(getTenant());
	this.eventManagement = SiteWhere.getServer().getDeviceEventManagement(getTenant());

	getLifecycleComponents().clear();
	for (IDeviceEventFilter filter : filters) {
	    startNestedComponent(filter, true);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
     */
    @Override
    public void stop() throws SiteWhereException {
	for (IDeviceEventFilter filter : filters) {
	    filter.stop();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.event.processor.OutboundEventProcessor#
     * onMeasurements(com. sitewhere.spi.device.event.IDeviceMeasurements)
     */
    @Override
    public final void onMeasurements(IDeviceMeasurements measurements) throws SiteWhereException {
	if (!isFiltered(measurements)) {
	    onMeasurementsNotFiltered(measurements);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.processor.IFilteredOutboundEventProcessor#
     * onMeasurementsNotFiltered(com.sitewhere.spi.device.event.
     * IDeviceMeasurements)
     */
    @Override
    public void onMeasurementsNotFiltered(IDeviceMeasurements measurements) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.event.processor.OutboundEventProcessor#onLocation(
     * com. sitewhere .spi.device.event.IDeviceLocation)
     */
    @Override
    public final void onLocation(IDeviceLocation location) throws SiteWhereException {
	if (!isFiltered(location)) {
	    onLocationNotFiltered(location);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.processor.IFilteredOutboundEventProcessor#
     * onLocationNotFiltered(com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocationNotFiltered(IDeviceLocation location) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.event.processor.OutboundEventProcessor#onAlert(com.
     * sitewhere .spi.device.event.IDeviceAlert)
     */
    @Override
    public final void onAlert(IDeviceAlert alert) throws SiteWhereException {
	if (!isFiltered(alert)) {
	    onAlertNotFiltered(alert);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.processor.IFilteredOutboundEventProcessor#
     * onAlertNotFiltered(com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlertNotFiltered(IDeviceAlert alert) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.event.processor.OutboundEventProcessor#onStateChange
     * (com. sitewhere.spi.device.event.IDeviceStateChange)
     */
    @Override
    public final void onStateChange(IDeviceStateChange state) throws SiteWhereException {
	if (!isFiltered(state)) {
	    onStateChangeNotFiltered(state);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.processor.IFilteredOutboundEventProcessor#
     * onStateChangeNotFiltered(com.sitewhere.spi.device.event.
     * IDeviceStateChange)
     */
    @Override
    public void onStateChangeNotFiltered(IDeviceStateChange state) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.event.processor.OutboundEventProcessor#
     * onCommandInvocation
     * (com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public final void onCommandInvocation(IDeviceCommandInvocation invocation) throws SiteWhereException {
	if (!isFiltered(invocation)) {
	    onCommandInvocationNotFiltered(invocation);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.processor.IFilteredOutboundEventProcessor#
     * onCommandInvocationNotFiltered
     * (com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void onCommandInvocationNotFiltered(IDeviceCommandInvocation invocation) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.event.processor.OutboundEventProcessor#
     * onCommandResponse(com .sitewhere.spi.device.event.IDeviceCommandResponse)
     */
    @Override
    public final void onCommandResponse(IDeviceCommandResponse response) throws SiteWhereException {
	if (!isFiltered(response)) {
	    onCommandResponseNotFiltered(response);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.processor.IFilteredOutboundEventProcessor#
     * onCommandResponseNotFiltered(com.sitewhere.spi.device.event.
     * IDeviceCommandResponse)
     */
    @Override
    public void onCommandResponseNotFiltered(IDeviceCommandResponse response) throws SiteWhereException {
    }

    /**
     * Indicates if an event is filtered.
     * 
     * @param event
     * @return
     * @throws SiteWhereException
     */
    protected boolean isFiltered(IDeviceEvent event) throws SiteWhereException {
	IDeviceAssignment assignment = deviceManagement.getDeviceAssignmentByToken(event.getDeviceAssignmentToken());
	if (assignment == null) {
	    throw new SiteWhereException("Device assignment for event not found.");
	}
	IDevice device = deviceManagement.getDeviceByHardwareId(assignment.getDeviceHardwareId());
	if (device == null) {
	    throw new SiteWhereException("Device assignment references unknown device.");
	}
	for (IDeviceEventFilter filter : filters) {
	    if (filter.isFiltered(event, device, assignment)) {
		return true;
	    }
	}
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.processor.IFilteredOutboundEventProcessor#
     * getFilters ()
     */
    public List<IDeviceEventFilter> getFilters() {
	return filters;
    }

    public void setFilters(List<IDeviceEventFilter> filters) {
	this.filters = filters;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    public IDeviceEventManagement getEventManagement() {
	return eventManagement;
    }

    public void setEventManagement(IDeviceEventManagement eventManagement) {
	this.eventManagement = eventManagement;
    }
}