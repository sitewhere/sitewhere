/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.destination;

import java.util.List;

import com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor;
import com.sitewhere.commands.spi.ICommandDeliveryProvider;
import com.sitewhere.commands.spi.ICommandDestination;
import com.sitewhere.commands.spi.ICommandExecutionEncoder;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Default implementation of {@link ICommandDestination}.
 * 
 * @param <T>
 */
public class CommandDestination<T, P> extends TenantEngineLifecycleComponent implements ICommandDestination<T, P> {

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
     * @see
     * com.sitewhere.commands.spi.ICommandDestination#deliverCommand(com.sitewhere.
     * spi.device.command.IDeviceCommandExecution,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public void deliverCommand(IDeviceCommandExecution execution, IDeviceNestingContext nesting,
	    List<IDeviceAssignment> assignments) throws SiteWhereException {
	T encoded = getCommandExecutionEncoder().encode(execution, nesting, assignments);
	if (encoded != null) {
	    P params = getCommandDeliveryParameterExtractor().extractDeliveryParameters(nesting, assignments,
		    execution);
	    getCommandDeliveryProvider().deliver(nesting, assignments, execution, encoded, params);
	} else {
	    getLogger().info("Skipping command delivery. Encoder returned null.");
	}
    }

    /*
     * @see com.sitewhere.commands.spi.ICommandDestination#deliverSystemCommand(com.
     * sitewhere.spi.device.command.ISystemCommand,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public void deliverSystemCommand(ISystemCommand command, IDeviceNestingContext nesting,
	    List<IDeviceAssignment> assignments) throws SiteWhereException {
	T encoded = getCommandExecutionEncoder().encodeSystemCommand(command, nesting, assignments);
	if (encoded != null) {
	    P params = getCommandDeliveryParameterExtractor().extractDeliveryParameters(nesting, assignments, null);
	    getCommandDeliveryProvider().deliverSystemCommand(nesting, assignments, encoded, params);
	} else {
	    getLogger().info("Skipping system command delivery. Encoder returned null.");
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for initializing processing strategy.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize execution encoder.
	init.addInitializeStep(this, getCommandExecutionEncoder(), true);

	// Initialize parameter extractor.
	init.addInitializeStep(this, getCommandDeliveryParameterExtractor(), true);

	// Initialize delivery provider.
	init.addInitializeStep(this, getCommandDeliveryProvider(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for starting processing strategy.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start execution encoder.
	start.addStartStep(this, getCommandExecutionEncoder(), true);

	// Start parameter extractor.
	start.addStartStep(this, getCommandDeliveryParameterExtractor(), true);

	// Start delivery provider.
	start.addStartStep(this, getCommandDeliveryProvider(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#getComponentName()
     */
    @Override
    public String getComponentName() {
	return getClass().getSimpleName() + " (" + getDestinationId() + ")";
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
	// Composite step for stopping processing strategy.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop delivery provider.
	stop.addStopStep(this, getCommandDeliveryProvider());

	// Stop parameter extractor.
	stop.addStopStep(this, getCommandDeliveryParameterExtractor());

	// Stop execution encoder.
	stop.addStopStep(this, getCommandExecutionEncoder());

	// Execute shutdown steps.
	stop.execute(monitor);
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