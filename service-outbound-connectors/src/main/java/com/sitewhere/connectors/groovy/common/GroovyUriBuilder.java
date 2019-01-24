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
import com.sitewhere.groovy.IGroovyVariables;
import com.sitewhere.microservice.groovy.GroovyComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.lang.Binding;

/**
 * Implementation of {@link IUriBuilder} that uses a Groovy script to build the
 * URI.
 */
public class GroovyUriBuilder extends GroovyComponent implements IUriBuilder {

    public GroovyUriBuilder() {
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
	Binding binding = new Binding();
	binding.setVariable(IGroovyVariables.VAR_EVENT_CONTEXT, context);
	binding.setVariable(IGroovyVariables.VAR_EVENT, event);
	binding.setVariable(IGroovyVariables.VAR_ASSIGNMENT, assignment);
	binding.setVariable(IGroovyVariables.VAR_DEVICE, device);
	binding.setVariable(IGroovyVariables.VAR_LOGGER, getLogger());
	return (String) run(binding);
    }
}