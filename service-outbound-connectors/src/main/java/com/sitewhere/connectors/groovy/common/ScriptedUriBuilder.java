/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.groovy.common;

import com.sitewhere.connectors.spi.IOutboundConnector;
import com.sitewhere.connectors.spi.common.IUriBuilder;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.microservice.scripting.ScriptingComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;

/**
 * Implementation of {@link IUriBuilder} that uses a script to build the URI.
 */
public class ScriptedUriBuilder extends ScriptingComponent<String> implements IUriBuilder {

    public ScriptedUriBuilder() {
	super(LifecycleComponentType.Other);
    }

    /*
     * @see com.sitewhere.connectors.spi.common.IUriBuilder#buildUri(com.sitewhere.
     * connectors.spi.IOutboundConnector,
     * com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceEvent)
     */
    @Override
    public String buildUri(IOutboundConnector connector, IDeviceEventContext context, IDeviceEvent event)
	    throws SiteWhereException {
	// These should be cached, so no performance hit.
	IDeviceAssignment assignment = connector.getDeviceManagement()
		.getDeviceAssignment(event.getDeviceAssignmentId());
	IDevice device = connector.getDeviceManagement().getDevice(assignment.getDeviceId());

	// Create Groovy binding with handles to everything.
	Binding binding = createBindingFor(this);
	binding.setVariable(IScriptVariables.VAR_EVENT_CONTEXT, context);
	binding.setVariable(IScriptVariables.VAR_EVENT, event);
	binding.setVariable(IScriptVariables.VAR_ASSIGNMENT, assignment);
	binding.setVariable(IScriptVariables.VAR_DEVICE, device);
	return (String) run(binding);
    }
}