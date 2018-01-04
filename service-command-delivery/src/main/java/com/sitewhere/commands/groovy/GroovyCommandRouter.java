/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.groovy;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;

import com.sitewhere.groovy.IGroovyVariables;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.device.communication.ICommandDestination;
import com.sitewhere.spi.device.communication.IOutboundCommandRouter;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Implementation of {@link IOutboundCommandRouter} that uses Groovy scripts to
 * perform routing logic.
 * 
 * @author Derek
 */
public class GroovyCommandRouter extends TenantEngineLifecycleComponent implements IOutboundCommandRouter {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** List of available command destinations */
    private List<ICommandDestination<?, ?>> commandDestinations;

    /** Groovy configuration */
    private GroovyConfiguration groovyConfiguration;

    /** Path to script used for routing custom commands */
    private String scriptPath;

    public GroovyCommandRouter() {
	super(LifecycleComponentType.CommandRouter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.communication.IOutboundCommandRouter#initialize(
     * java.util.List)
     */
    @Override
    public void initialize(List<ICommandDestination<?, ?>> destinations) throws SiteWhereException {
	this.commandDestinations = destinations;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IOutboundCommandRouter#
     * routeCommand(com.sitewhere.spi.device.command.IDeviceCommandExecution,
     * com.sitewhere.spi.device.IDeviceNestingContext,
     * com.sitewhere.spi.device.IDeviceAssignment)
     */
    @Override
    public void routeCommand(IDeviceCommandExecution execution, IDeviceNestingContext nesting,
	    IDeviceAssignment assignment) throws SiteWhereException {
	route(execution, null, nesting, assignment);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IOutboundCommandRouter#
     * routeSystemCommand(com.sitewhere.spi.device.command.ISystemCommand,
     * com.sitewhere.spi.device.IDeviceNestingContext,
     * com.sitewhere.spi.device.IDeviceAssignment)
     */
    @Override
    public void routeSystemCommand(ISystemCommand command, IDeviceNestingContext nesting, IDeviceAssignment assignment)
	    throws SiteWhereException {
	route(null, command, nesting, assignment);
    }

    /**
     * Route either a custom command or system command based on logic determined in
     * a Groovy script.
     * 
     * @param execution
     * @param system
     * @param nesting
     * @param assignment
     * @throws SiteWhereException
     */
    protected void route(IDeviceCommandExecution execution, ISystemCommand system, IDeviceNestingContext nesting,
	    IDeviceAssignment assignment) throws SiteWhereException {
	try {
	    Binding binding = new Binding();
	    binding.setVariable(IGroovyVariables.VAR_COMMAND_EXECUTION, execution);
	    binding.setVariable(IGroovyVariables.VAR_SYSTEM_COMMAND, system);
	    binding.setVariable(IGroovyVariables.VAR_NESTING_CONTEXT, nesting);
	    binding.setVariable(IGroovyVariables.VAR_ASSIGNMENT, assignment);
	    binding.setVariable(IGroovyVariables.VAR_LOGGER, LOGGER);
	    LOGGER.debug("About to route command using script '" + getScriptPath() + "'");
	    String target = (String) getGroovyConfiguration().getGroovyScriptEngine().run(getScriptPath(), binding);
	    if (target != null) {
		for (ICommandDestination<?, ?> destination : getCommandDestinations()) {
		    if (target.equals(destination.getDestinationId())) {
			if (execution != null) {
			    destination.deliverCommand(execution, nesting, assignment);
			} else if (system != null) {
			    destination.deliverSystemCommand(system, nesting, assignment);
			}
		    }
		}
	    } else {
		LOGGER.warn("Groovy command router did not return a command destination id.");
	    }
	} catch (ResourceException e) {
	    throw new SiteWhereException("Unable to access Groovy command router script.", e);
	} catch (ScriptException e) {
	    throw new SiteWhereException("Unable to run Groovy command router script.", e);
	} catch (CompilationFailedException e) {
	    throw new SiteWhereException("Error compiling Groovy script.", e);
	} catch (Throwable e) {
	    throw new SiteWhereException("Unhandled exception in Groovy command router script.", e);
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

    public List<ICommandDestination<?, ?>> getCommandDestinations() {
	return commandDestinations;
    }

    public void setCommandDestinations(List<ICommandDestination<?, ?>> commandDestinations) {
	this.commandDestinations = commandDestinations;
    }
}