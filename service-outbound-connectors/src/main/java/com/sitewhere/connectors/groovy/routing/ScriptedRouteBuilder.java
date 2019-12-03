/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.groovy.routing;

import com.sitewhere.connectors.spi.routing.IRouteBuilder;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.microservice.scripting.ScriptingComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;
import com.sitewhere.spi.microservice.scripting.ScriptScope;
import com.sitewhere.spi.microservice.scripting.ScriptType;

/**
 * Uses script to build routes for event processor routing.
 */
public class ScriptedRouteBuilder extends ScriptingComponent<String> implements IRouteBuilder<String> {

    public ScriptedRouteBuilder() {
	super(LifecycleComponentType.OutboundEventProcessorFilter);
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.routing.IRouteBuilder#build(com.sitewhere.spi.
     * device.event.IDeviceEvent, com.sitewhere.spi.device.IDevice,
     * com.sitewhere.spi.device.IDeviceAssignment)
     */
    @Override
    public String build(IDeviceEvent event, IDevice device, IDeviceAssignment assignment) throws SiteWhereException {
	Binding binding = new Binding();
	binding.setVariable(IScriptVariables.VAR_LOGGER, getLogger());
	binding.setVariable("event", event);
	binding.setVariable("device", device);
	binding.setVariable("assignment", assignment);
	try {
	    Object result = run(ScriptScope.TenantEngine, ScriptType.Managed, binding);
	    if (!(result instanceof String)) {
		throw new SiteWhereException("Groovy route builder expected script to return a String.");
	    }
	    return (String) result;
	} catch (SiteWhereException e) {
	    throw new SiteWhereException("Unable to run route builder script.", e);
	}
    }
}