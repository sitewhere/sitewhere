/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.connectors.groovy;

import com.sitewhere.connectors.SerialOutboundConnector;
import com.sitewhere.microservice.api.device.DeviceManagementRequestBuilder;
import com.sitewhere.microservice.api.event.DeviceEventRequestBuilder;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.microservice.scripting.ScriptingComponent;
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
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;

/**
 * Outbound event processor that uses a Groovy script to process events.
 */
public class ScriptedOutboundConnector extends SerialOutboundConnector {

    /** Supports building device management entities */
    private DeviceManagementRequestBuilder deviceBuilder;

    /** Supports building various types of device events */
    private DeviceEventRequestBuilder eventsBuilder;

    /** Wrapped scripting component */
    private ConnectorScriptComponent scriptingComponent;

    /*
     * @see
     * com.sitewhere.connectors.FilteredOutboundConnector#initialize(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Add wrappers for APIs.
	this.deviceBuilder = new DeviceManagementRequestBuilder(getDeviceManagement());
	this.eventsBuilder = new DeviceEventRequestBuilder(getDeviceManagement(), getDeviceEventManagement());

	// Create nested scripting component and pass configuration.
	this.scriptingComponent = new ConnectorScriptComponent();
	initializeNestedComponent(getScriptingComponent(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.connectors.FilteredOutboundConnector#start(com.sitewhere.spi.
     * microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Required for filters.
	super.start(monitor);

	// Start nested Groovy component.
	startNestedComponent(getScriptingComponent(), monitor, true);
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
	stopNestedComponent(getScriptingComponent(), monitor);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onMeasurement(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurement)
     */
    @Override
    public void onMeasurement(IDeviceEventContext context, IDeviceMeasurement event) throws SiteWhereException {
	getScriptingComponent().processEvent(context, event);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onLocation(com.sitewhere.spi
     * .device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocation(IDeviceEventContext context, IDeviceLocation event) throws SiteWhereException {
	getScriptingComponent().processEvent(context, event);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onAlert(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlert(IDeviceEventContext context, IDeviceAlert event) throws SiteWhereException {
	getScriptingComponent().processEvent(context, event);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onStateChange(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceStateChange)
     */
    @Override
    public void onStateChange(IDeviceEventContext context, IDeviceStateChange event) throws SiteWhereException {
	getScriptingComponent().processEvent(context, event);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onCommandInvocation(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void onCommandInvocation(IDeviceEventContext context, IDeviceCommandInvocation event)
	    throws SiteWhereException {
	getScriptingComponent().processEvent(context, event);
    }

    /*
     * @see com.sitewhere.connectors.SerialOutboundConnector#onCommandResponse(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandResponse)
     */
    @Override
    public void onCommandResponse(IDeviceEventContext context, IDeviceCommandResponse event) throws SiteWhereException {
	getScriptingComponent().processEvent(context, event);
    }

    /**
     * Provides script processing for events.
     */
    public class ConnectorScriptComponent extends ScriptingComponent<Void> {

	/**
	 * Perform custom event processing in a Groovy script.
	 * 
	 * @param context
	 * @param event
	 * @throws SiteWhereException
	 */
	public void processEvent(IDeviceEventContext context, IDeviceEvent event) throws SiteWhereException {
	    // These should be cached, so no performance hit.
	    IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignment(event.getDeviceAssignmentId());
	    IDevice device = getDeviceManagement().getDevice(assignment.getDeviceId());

	    // Create Groovy binding with handles to everything.
	    Binding binding = createBindingFor(this);
	    binding.setVariable(IScriptVariables.VAR_EVENT_CONTEXT, context);
	    binding.setVariable(IScriptVariables.VAR_EVENT, event);
	    binding.setVariable(IScriptVariables.VAR_ASSIGNMENT, assignment);
	    binding.setVariable(IScriptVariables.VAR_DEVICE, device);
	    binding.setVariable(IScriptVariables.VAR_DEVICE_MANAGEMENT_BUILDER, deviceBuilder);
	    binding.setVariable(IScriptVariables.VAR_EVENT_MANAGEMENT_BUILDER, eventsBuilder);

	    run(binding);
	}
    }

    protected ConnectorScriptComponent getScriptingComponent() {
	return scriptingComponent;
    }
}