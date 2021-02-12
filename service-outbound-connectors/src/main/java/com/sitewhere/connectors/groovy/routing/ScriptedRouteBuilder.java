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
	    Object result = run(binding);
	    if (!(result instanceof String)) {
		throw new SiteWhereException("Groovy route builder expected script to return a String.");
	    }
	    return (String) result;
	} catch (SiteWhereException e) {
	    throw new SiteWhereException("Unable to run route builder script.", e);
	}
    }
}