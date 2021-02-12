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
package com.sitewhere.connectors.groovy.filter;

import com.sitewhere.connectors.filter.DeviceEventFilter;
import com.sitewhere.connectors.microservice.OutboundConnectorsMicroservice;
import com.sitewhere.connectors.spi.IDeviceEventFilter;
import com.sitewhere.microservice.api.device.DeviceManagementRequestBuilder;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.microservice.scripting.ScriptingComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;

/**
 * Implementation of {@link IDeviceEventFilter} that uses a Groovy script to
 * determine whether events should be included or filtered. If the script
 * returns true, the event is excluded. If the script return false, the event is
 * included.
 */
public class ScriptedFilter extends DeviceEventFilter {

    /** Wrapped scripting component */
    private FilterScriptComponent scriptingComponent;

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Create nested scripting component and pass configuration.
	this.scriptingComponent = new FilterScriptComponent();
	initializeNestedComponent(getScriptingComponent(), monitor, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.event.processor.filter.DeviceEventFilter#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	// Start nested scripting component.
	startNestedComponent(getScriptingComponent(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.connectors.FilteredOutboundConnector#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);

	// Stop nested scripting component.
	stopNestedComponent(getScriptingComponent(), monitor);
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IDeviceEventFilter#isFiltered(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceEvent)
     */
    @Override
    public boolean isFiltered(IDeviceEventContext context, IDeviceEvent event) throws SiteWhereException {
	return getScriptingComponent().isFiltered(context, event);
    }

    /**
     * Provides script processing for filtering.
     */
    public class FilterScriptComponent extends ScriptingComponent<Boolean> implements IDeviceEventFilter {

	/*
	 * @see
	 * com.sitewhere.connectors.spi.IDeviceEventFilter#isFiltered(com.sitewhere.spi.
	 * device.event.IDeviceEventContext,
	 * com.sitewhere.spi.device.event.IDeviceEvent)
	 */
	@Override
	public boolean isFiltered(IDeviceEventContext context, IDeviceEvent event) throws SiteWhereException {
	    IDeviceManagement deviceManagement = ((OutboundConnectorsMicroservice) getMicroservice())
		    .getDeviceManagement();
	    IDeviceAssignment assignment = deviceManagement.getDeviceAssignment(event.getDeviceAssignmentId());
	    IDevice device = deviceManagement.getDevice(assignment.getDeviceId());

	    Binding binding = createBindingFor(this);
	    binding.setVariable(IScriptVariables.VAR_EVENT_CONTEXT, context);
	    binding.setVariable(IScriptVariables.VAR_EVENT, event);
	    binding.setVariable(IScriptVariables.VAR_ASSIGNMENT, assignment);
	    binding.setVariable(IScriptVariables.VAR_DEVICE, device);
	    binding.setVariable(IScriptVariables.VAR_DEVICE_MANAGEMENT_BUILDER,
		    new DeviceManagementRequestBuilder(deviceManagement));
	    return run(binding);
	}
    }

    protected FilterScriptComponent getScriptingComponent() {
	return scriptingComponent;
    }
}