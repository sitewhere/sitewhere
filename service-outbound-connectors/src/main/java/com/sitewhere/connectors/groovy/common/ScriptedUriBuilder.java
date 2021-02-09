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