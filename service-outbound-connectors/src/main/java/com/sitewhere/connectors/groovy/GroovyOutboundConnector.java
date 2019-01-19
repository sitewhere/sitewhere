/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.groovy;

import com.sitewhere.connectors.SerialOutboundConnector;
import com.sitewhere.groovy.IGroovyVariables;
import com.sitewhere.microservice.groovy.GroovyComponent;
import com.sitewhere.rest.model.device.event.request.scripting.DeviceEventRequestBuilder;
import com.sitewhere.rest.model.device.request.scripting.DeviceManagementRequestBuilder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import groovy.lang.Binding;

/**
 * Outbound event processor that uses a Groovy script to process events.
 * 
 * @author Derek
 */
public class GroovyOutboundConnector extends SerialOutboundConnector {

    /** Unique script id to execute */
    private String scriptId;

    /** Supports building device management entities */
    private DeviceManagementRequestBuilder deviceBuilder;

    /** Supports building various types of device events */
    private DeviceEventRequestBuilder eventsBuilder;

    /** Wrapped Groovy component */
    private GroovyComponent groovyComponent;

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Add wrappers for APIs.
	this.deviceBuilder = new DeviceManagementRequestBuilder(getDeviceManagement());
	this.eventsBuilder = new DeviceEventRequestBuilder(getDeviceManagement(), getDeviceEventManagement());

	// Create nested Groovy component and pass configuration.
	this.groovyComponent = new GroovyComponent();
	getGroovyComponent().setScriptId(getScriptId());
	getGroovyComponent().setNumThreads(1);
	initializeNestedComponent(getGroovyComponent(), monitor, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#start
     * (com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Required for filters.
	super.start(monitor);

	// Start nested Groovy component.
	startNestedComponent(getGroovyComponent(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.connectors.FilteredOutboundConnector#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Required for filters.
	super.stop(monitor);

	// Stop nested Groovy component.
	stopNestedComponent(getGroovyComponent(), monitor);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onMeasurement(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurement)
     */
    @Override
    public void onMeasurement(IDeviceEventContext context, IDeviceMeasurement mx) throws SiteWhereException {
	processEvent(context, mx);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onLocation(com.sitewhere.spi
     * .device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocation(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
	processEvent(context, location);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onAlert(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlert(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
	processEvent(context, alert);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onStateChange(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceStateChange)
     */
    @Override
    public void onStateChange(IDeviceEventContext context, IDeviceStateChange state) throws SiteWhereException {
	processEvent(context, state);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onCommandInvocation(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void onCommandInvocation(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException {
	processEvent(context, invocation);
    }

    /*
     * @see com.sitewhere.connectors.SerialOutboundConnector#onCommandResponse(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandResponse)
     */
    @Override
    public void onCommandResponse(IDeviceEventContext context, IDeviceCommandResponse response)
	    throws SiteWhereException {
	processEvent(context, response);
    }

    /**
     * Perform custom event processing in a Groovy script.
     * 
     * @param context
     * @param event
     * @throws SiteWhereException
     */
    protected void processEvent(IDeviceEventContext context, IDeviceEvent event) throws SiteWhereException {
	// These should be cached, so no performance hit.
	IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignment(event.getDeviceAssignmentId());
	IDevice device = getDeviceManagement().getDevice(assignment.getDeviceId());

	// Create Groovy binding with handles to everything.
	Binding binding = new Binding();
	binding.setVariable(IGroovyVariables.VAR_EVENT_CONTEXT, context);
	binding.setVariable(IGroovyVariables.VAR_EVENT, event);
	binding.setVariable(IGroovyVariables.VAR_ASSIGNMENT, assignment);
	binding.setVariable(IGroovyVariables.VAR_DEVICE, device);
	binding.setVariable(IGroovyVariables.VAR_DEVICE_MANAGEMENT_BUILDER, deviceBuilder);
	binding.setVariable(IGroovyVariables.VAR_EVENT_MANAGEMENT_BUILDER, eventsBuilder);
	binding.setVariable(IGroovyVariables.VAR_LOGGER, getLogger());

	getGroovyComponent().run(binding);
    }

    protected GroovyComponent getGroovyComponent() {
	return groovyComponent;
    }

    public String getScriptId() {
	return scriptId;
    }

    public void setScriptId(String scriptId) {
	this.scriptId = scriptId;
    }
}