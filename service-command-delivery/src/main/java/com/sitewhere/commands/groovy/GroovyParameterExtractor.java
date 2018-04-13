/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.groovy;

import com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor;
import com.sitewhere.groovy.IGroovyVariables;
import com.sitewhere.microservice.groovy.GroovyComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.lang.Binding;

/**
 * Common base class for Groovy command delivery parameter extractors.
 * 
 * @author Derek
 *
 * @param <T>
 */
public class GroovyParameterExtractor<T> extends GroovyComponent implements ICommandDeliveryParameterExtractor<T> {

    public GroovyParameterExtractor() {
	super(LifecycleComponentType.CommandParameterExtractor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.communication.ICommandDeliveryParameterExtractor
     * #extractDeliveryParameters(com.sitewhere.spi.device. IDeviceNestingContext,
     * com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.command.IDeviceCommandExecution)
     */
    @Override
    @SuppressWarnings("unchecked")
    public T extractDeliveryParameters(IDeviceNestingContext nesting, IDeviceAssignment assignment,
	    IDeviceCommandExecution execution) throws SiteWhereException {
	try {
	    Binding binding = new Binding();
	    binding.setVariable(IGroovyVariables.VAR_NESTING_CONTEXT, nesting);
	    binding.setVariable(IGroovyVariables.VAR_ASSIGNMENT, assignment);
	    binding.setVariable(IGroovyVariables.VAR_LOGGER, getLogger());
	    return (T) run(binding);
	} catch (SiteWhereException e) {
	    throw new SiteWhereException("Unable to run parameter extractor script.", e);
	}
    }
}