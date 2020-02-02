/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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
import com.sitewhere.spi.microservice.scripting.ScriptScope;
import com.sitewhere.spi.microservice.scripting.ScriptType;

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
	    return run(ScriptScope.TenantEngine, ScriptType.Managed, binding);
	}
    }

    protected FilterScriptComponent getScriptingComponent() {
	return scriptingComponent;
    }
}