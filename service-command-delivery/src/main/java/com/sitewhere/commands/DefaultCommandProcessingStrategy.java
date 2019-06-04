/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands;

import java.util.List;

import com.sitewhere.commands.routing.CommandRoutingLogic;
import com.sitewhere.commands.spi.ICommandExecutionBuilder;
import com.sitewhere.commands.spi.ICommandProcessingStrategy;
import com.sitewhere.commands.spi.ICommandTargetResolver;
import com.sitewhere.commands.spi.IOutboundCommandRouter;
import com.sitewhere.commands.spi.kafka.IUndeliveredCommandInvocationsProducer;
import com.sitewhere.commands.spi.microservice.ICommandDeliveryMicroservice;
import com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Default implementation of {@link ICommandProcessingStrategy}.
 * 
 * @author Derek
 */
public class DefaultCommandProcessingStrategy extends TenantEngineLifecycleComponent
	implements ICommandProcessingStrategy {

    /** Configured command target resolver */
    private ICommandTargetResolver commandTargetResolver = new DefaultCommandTargetResolver();

    /** Configured command execution builder */
    private ICommandExecutionBuilder commandExecutionBuilder = new DefaultCommandExecutionBuilder();

    public DefaultCommandProcessingStrategy() {
	super(LifecycleComponentType.CommandProcessingStrategy);
    }

    /*
     * @see
     * com.sitewhere.commands.spi.ICommandProcessingStrategy#deliverCommand(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void deliverCommand(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException {
	getLogger().debug("Command processing strategy handling invocation.");
	IDeviceCommand command = getDeviceManagementApiChannel().getDeviceCommandByToken(invocation.getCommandToken());
	if (command != null) {
	    IDeviceCommandExecution execution = getCommandExecutionBuilder().createExecution(command, invocation);
	    List<IDeviceAssignment> assignments = getCommandTargetResolver().resolveTargets(invocation);
	    for (IDeviceAssignment assignment : assignments) {
		IDevice device = getDeviceManagementApiChannel().getDevice(assignment.getDeviceId());
		if (device == null) {
		    throw new SiteWhereException("Targeted assignment references device that does not exist.");
		}

		IDeviceNestingContext nesting = NestedDeviceSupport.calculateNestedDeviceInformation(device,
			getTenantEngine().getTenant());
		CommandRoutingLogic.routeCommand(getOutboundCommandRouter(), getUndeliveredCommandInvocationsProducer(),
			context, execution, nesting, assignment);
	    }
	} else {
	    throw new SiteWhereException("Invalid command referenced from invocation.");
	}
    }

    /*
     * @see
     * com.sitewhere.commands.spi.ICommandProcessingStrategy#deliverSystemCommand(
     * com.sitewhere.spi.device.event.IDeviceEventContext, java.lang.String,
     * com.sitewhere.spi.device.command.ISystemCommand)
     */
    @Override
    public void deliverSystemCommand(IDeviceEventContext context, String deviceToken, ISystemCommand command)
	    throws SiteWhereException {
	getLogger().debug("Command processing strategy handling system command invocation.");
	IDevice device = getDeviceManagementApiChannel().getDeviceByToken(deviceToken);
	if (device == null) {
	    throw new SiteWhereException("Targeted assignment references device that does not exist.");
	}
	IDeviceAssignment assignment = getDeviceManagementApiChannel()
		.getDeviceAssignment(device.getDeviceAssignmentId());
	IDeviceNestingContext nesting = NestedDeviceSupport.calculateNestedDeviceInformation(device,
		getTenantEngine().getTenant());
	CommandRoutingLogic.routeSystemCommand(getOutboundCommandRouter(), command, nesting, assignment);
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

	// Initialize command execution builder.
	init.addInitializeStep(this, getCommandExecutionBuilder(), true);

	// Initialize command target resolver.
	init.addInitializeStep(this, getCommandTargetResolver(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for starting processing strategy.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start command execution builder.
	start.addStartStep(this, getCommandExecutionBuilder(), true);

	// Start command target resolver.
	start.addStartStep(this, getCommandTargetResolver(), true);

	// Execute startup steps.
	start.execute(monitor);
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

	// Stop command target resolver.
	stop.addStopStep(this, getCommandTargetResolver());

	// Stop command execution builder.
	stop.addStopStep(this, getCommandExecutionBuilder());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    public ICommandTargetResolver getCommandTargetResolver() {
	return commandTargetResolver;
    }

    public void setCommandTargetResolver(ICommandTargetResolver commandTargetResolver) {
	this.commandTargetResolver = commandTargetResolver;
    }

    public ICommandExecutionBuilder getCommandExecutionBuilder() {
	return commandExecutionBuilder;
    }

    public void setCommandExecutionBuilder(ICommandExecutionBuilder commandExecutionBuilder) {
	this.commandExecutionBuilder = commandExecutionBuilder;
    }

    private IDeviceManagementApiChannel<?> getDeviceManagementApiChannel() {
	return ((ICommandDeliveryMicroservice) getMicroservice()).getDeviceManagementApiChannel();
    }

    private IOutboundCommandRouter getOutboundCommandRouter() {
	return ((ICommandDeliveryTenantEngine) getTenantEngine()).getOutboundCommandRouter();
    }

    private IUndeliveredCommandInvocationsProducer getUndeliveredCommandInvocationsProducer() {
	return ((ICommandDeliveryTenantEngine) getTenantEngine()).getUndeliveredCommandInvocationsProducer();
    }
}