/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.scripting;

import java.util.List;

import com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.microservice.scripting.ScriptingComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;
import com.sitewhere.spi.microservice.scripting.ScriptScope;
import com.sitewhere.spi.microservice.scripting.ScriptType;

/**
 * Common base class for scripted command delivery parameter extractors.
 * 
 * @param <T>
 */
public class ScriptedParameterExtractor<T> extends ScriptingComponent<T>
	implements ICommandDeliveryParameterExtractor<T> {

    public ScriptedParameterExtractor() {
	super(LifecycleComponentType.CommandParameterExtractor);
    }

    /*
     * @see com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor#
     * extractDeliveryParameters(com.sitewhere.spi.device.IDeviceNestingContext,
     * java.util.List, com.sitewhere.spi.device.command.IDeviceCommandExecution)
     */
    @Override
    public T extractDeliveryParameters(IDeviceNestingContext nesting, List<IDeviceAssignment> assignments,
	    IDeviceCommandExecution execution) throws SiteWhereException {
	try {
	    Binding binding = createBindingFor(this);
	    binding.setVariable(IScriptVariables.VAR_NESTING_CONTEXT, nesting);
	    binding.setVariable(IScriptVariables.VAR_ACTIVE_ASSIGNMENTS, assignments);
	    return run(ScriptScope.TenantEngine, ScriptType.Managed, binding);
	} catch (SiteWhereException e) {
	    throw new SiteWhereException("Unable to run parameter extractor script.", e);
	}
    }
}