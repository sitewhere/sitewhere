/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.initializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.event.spi.initializer.IEventModelInitializer;
import com.sitewhere.microservice.api.device.DeviceManagementRequestBuilder;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.DeviceEventRequestBuilder;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.model.ScriptedModelInitializer;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;
import com.sitewhere.spi.microservice.scripting.ScriptScope;
import com.sitewhere.spi.microservice.scripting.ScriptType;

/**
 * Implementation of {@link IEventModelInitializer} that delegates creation
 * logic to a script.
 */
public class ScriptedEventModelInitializer extends ScriptedModelInitializer<Void> implements IEventModelInitializer {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(ScriptedEventModelInitializer.class);

    /*
     * @see
     * com.sitewhere.event.spi.initializer.IEventModelInitializer#initialize(com.
     * sitewhere.microservice.api.device.IDeviceManagement,
     * com.sitewhere.microservice.api.event.IDeviceEventManagement)
     */
    @Override
    public void initialize(IDeviceManagement deviceManagement, IDeviceEventManagement eventManagement)
	    throws SiteWhereException {
	Binding binding = new Binding();
	binding.setVariable(IScriptVariables.VAR_LOGGER, LOGGER);
	binding.setVariable(IScriptVariables.VAR_DEVICE_MANAGEMENT_BUILDER,
		new DeviceManagementRequestBuilder(deviceManagement));
	binding.setVariable(IScriptVariables.VAR_EVENT_MANAGEMENT_BUILDER,
		new DeviceEventRequestBuilder(deviceManagement, eventManagement));
	run(ScriptScope.TenantEngine, ScriptType.Bootstrap, binding);
    }
}
