/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.groovy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;

import com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor;
import com.sitewhere.groovy.IGroovyVariables;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Common base class for Groovy command delivery parameter extractors.
 * 
 * @author Derek
 *
 * @param <T>
 */
public class GroovyParameterExtractor<T> extends TenantEngineLifecycleComponent
	implements ICommandDeliveryParameterExtractor<T> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Groovy configuration */
    private GroovyConfiguration groovyConfiguration;

    /** Path to script used for decoder */
    private String scriptPath;

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
	    binding.setVariable(IGroovyVariables.VAR_LOGGER, LOGGER);
	    return (T) getGroovyConfiguration().getGroovyScriptEngine().run(getScriptPath(), binding);
	} catch (ResourceException e) {
	    throw new SiteWhereException("Unable to access Groovy decoder script.", e);
	} catch (ScriptException e) {
	    throw new SiteWhereException("Unable to run Groovy decoder script.", e);
	} catch (CompilationFailedException e) {
	    throw new SiteWhereException("Error compiling Groovy script.", e);
	} catch (Throwable e) {
	    throw new SiteWhereException("Unhandled exception in Groovy decoder script.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public GroovyConfiguration getGroovyConfiguration() {
	return groovyConfiguration;
    }

    public void setGroovyConfiguration(GroovyConfiguration groovyConfiguration) {
	this.groovyConfiguration = groovyConfiguration;
    }

    public String getScriptPath() {
	return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
	this.scriptPath = scriptPath;
    }
}