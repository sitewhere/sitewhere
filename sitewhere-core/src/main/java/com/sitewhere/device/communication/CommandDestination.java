/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.device.communication.ICommandDeliveryParameterExtractor;
import com.sitewhere.spi.device.communication.ICommandDeliveryProvider;
import com.sitewhere.spi.device.communication.ICommandDestination;
import com.sitewhere.spi.device.communication.ICommandExecutionEncoder;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Default implementation of {@link ICommandDestination}.
 * 
 * @author Derek
 * 
 * @param <T>
 */
public class CommandDestination<T, P> extends TenantLifecycleComponent implements ICommandDestination<T, P> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Unique destination id */
    private String destinationId;

    /** Configured command execution encoder */
    private ICommandExecutionEncoder<T> commandExecutionEncoder;

    /** Configured command delivery parameter extractor */
    private ICommandDeliveryParameterExtractor<P> commandDeliveryParameterExtractor;

    /** Configured command delivery provider */
    private ICommandDeliveryProvider<T, P> commandDeliveryProvider;

    public CommandDestination() {
	super(LifecycleComponentType.CommandDestination);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.communication.ICommandDestination#deliverCommand
     * (com.sitewhere .spi.device.command.IDeviceCommandExecution,
     * com.sitewhere.spi.device.IDeviceNestingContext,
     * com.sitewhere.spi.device.IDeviceAssignment)
     */
    @Override
    public void deliverCommand(IDeviceCommandExecution execution, IDeviceNestingContext nesting,
	    IDeviceAssignment assignment) throws SiteWhereException {
	T encoded = getCommandExecutionEncoder().encode(execution, nesting, assignment);
	if (encoded != null) {
	    P params = getCommandDeliveryParameterExtractor().extractDeliveryParameters(nesting, assignment, execution);
	    getCommandDeliveryProvider().deliver(nesting, assignment, execution, encoded, params);
	} else {
	    LOGGER.info("Skipping command delivery. Encoder returned null.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICommandDestination#
     * deliverSystemCommand (com.sitewhere.spi.device.command.ISystemCommand,
     * com.sitewhere.spi.device.IDeviceNestingContext,
     * com.sitewhere.spi.device.IDeviceAssignment)
     */
    @Override
    public void deliverSystemCommand(ISystemCommand command, IDeviceNestingContext nesting,
	    IDeviceAssignment assignment) throws SiteWhereException {
	T encoded = getCommandExecutionEncoder().encodeSystemCommand(command, nesting, assignment);
	if (encoded != null) {
	    P params = getCommandDeliveryParameterExtractor().extractDeliveryParameters(nesting, assignment, null);
	    getCommandDeliveryProvider().deliverSystemCommand(nesting, assignment, encoded, params);
	} else {
	    LOGGER.info("Skipping system command delivery. Encoder returned null.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Clear the component list.
	getLifecycleComponents().clear();

	// Start command execution encoder.
	if (getCommandExecutionEncoder() == null) {
	    throw new SiteWhereException("No command execution encoder configured for destination.");
	}
	startNestedComponent(getCommandExecutionEncoder(), monitor, true);

	// Start command execution encoder.
	if (getCommandDeliveryParameterExtractor() == null) {
	    throw new SiteWhereException("No command delivery parameter extractor configured for destination.");
	}

	// Start command delivery provider.
	if (getCommandDeliveryProvider() == null) {
	    throw new SiteWhereException("No command delivery provider configured for destination.");
	}
	startNestedComponent(getCommandDeliveryProvider(), monitor, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#getComponentName()
     */
    @Override
    public String getComponentName() {
	return "Command Destination (" + getDestinationId() + ")";
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Stop command execution encoder.
	if (getCommandExecutionEncoder() != null) {
	    getCommandExecutionEncoder().lifecycleStop(monitor);
	}

	// Stop command delivery provider.
	if (getCommandDeliveryProvider() != null) {
	    getCommandDeliveryProvider().lifecycleStop(monitor);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICommandDestination#
     * getDestinationId()
     */
    public String getDestinationId() {
	return destinationId;
    }

    public void setDestinationId(String destinationId) {
	this.destinationId = destinationId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICommandDestination#
     * getCommandExecutionEncoder ()
     */
    public ICommandExecutionEncoder<T> getCommandExecutionEncoder() {
	return commandExecutionEncoder;
    }

    public void setCommandExecutionEncoder(ICommandExecutionEncoder<T> commandExecutionEncoder) {
	this.commandExecutionEncoder = commandExecutionEncoder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICommandDestination#
     * getCommandDeliveryParameterExtractor()
     */
    public ICommandDeliveryParameterExtractor<P> getCommandDeliveryParameterExtractor() {
	return commandDeliveryParameterExtractor;
    }

    public void setCommandDeliveryParameterExtractor(
	    ICommandDeliveryParameterExtractor<P> commandDeliveryParameterExtractor) {
	this.commandDeliveryParameterExtractor = commandDeliveryParameterExtractor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICommandDestination#
     * getCommandDeliveryProvider ()
     */
    public ICommandDeliveryProvider<T, P> getCommandDeliveryProvider() {
	return commandDeliveryProvider;
    }

    public void setCommandDeliveryProvider(ICommandDeliveryProvider<T, P> commandDeliveryProvider) {
	this.commandDeliveryProvider = commandDeliveryProvider;
    }
}