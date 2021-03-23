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
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Default implementation of {@link ICommandProcessingStrategy}.
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
	new CommandDeliverer(this, context, invocation).run();
    }

    /**
     * Runs command delivery logic in the context of a system user.
     */
    private class CommandDeliverer extends SystemUserRunnable {

	private IDeviceEventContext context;
	private IDeviceCommandInvocation invocation;

	public CommandDeliverer(ITenantEngineLifecycleComponent component, IDeviceEventContext context,
		IDeviceCommandInvocation invocation) {
	    super(component);
	    this.context = context;
	    this.invocation = invocation;
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#runAsSystemUser()
	 */
	@Override
	public void runAsSystemUser() throws SiteWhereException {
	    IDeviceCommand command = getDeviceManagement().getDeviceCommand(invocation.getDeviceCommandId());
	    if (command != null) {
		IDeviceCommandExecution execution = getCommandExecutionBuilder().createExecution(command, invocation);
		List<IDeviceAssignment> assignments = getCommandTargetResolver().resolveTargets(invocation);
		for (IDeviceAssignment assignment : assignments) {
		    IDevice device = getDeviceManagement().getDevice(assignment.getDeviceId());
		    if (device == null) {
			throw new SiteWhereException("Targeted assignment references device that does not exist.");
		    }

		    List<? extends IDeviceAssignment> active = getDeviceManagement()
			    .getActiveDeviceAssignments(device.getId());
		    IDeviceNestingContext nesting = NestedDeviceSupport.calculateNestedDeviceInformation(device,
			    getTenantEngine().getTenantResource());
		    CommandRoutingLogic.routeCommand(getOutboundCommandRouter(),
			    getUndeliveredCommandInvocationsProducer(), context, execution, nesting, active);
		}
	    } else {
		throw new SiteWhereException("Invalid command referenced from invocation.");
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.commands.spi.ICommandProcessingStrategy#deliverSystemCommand(
     * com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.command.ISystemCommand)
     */
    @Override
    public void deliverSystemCommand(IDeviceEventContext context, ISystemCommand command) throws SiteWhereException {
	getLogger().debug("Command processing strategy handling system command invocation.");
	new SystemCommandDeliverer(this, context, command).run();
    }

    /**
     * Runs command delivery logic in the context of a system user.
     */
    private class SystemCommandDeliverer extends SystemUserRunnable {

	private IDeviceEventContext context;
	private ISystemCommand command;

	public SystemCommandDeliverer(ITenantEngineLifecycleComponent component, IDeviceEventContext context,
		ISystemCommand command) {
	    super(component);
	    this.context = context;
	    this.command = command;
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#runAsSystemUser()
	 */
	@Override
	public void runAsSystemUser() throws SiteWhereException {
	    IDevice device = getDeviceManagement().getDeviceByToken(context.getDeviceToken());
	    if (device == null) {
		throw new SiteWhereException("Targeted assignment references device that does not exist.");
	    }

	    List<? extends IDeviceAssignment> assignments = getDeviceManagement()
		    .getActiveDeviceAssignments(device.getId());
	    IDeviceNestingContext nesting = NestedDeviceSupport.calculateNestedDeviceInformation(device,
		    getTenantEngine().getTenantResource());
	    CommandRoutingLogic.routeSystemCommand(getOutboundCommandRouter(), command, nesting, assignments);
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

	// Initialize command execution builder.
	init.addInitializeStep(this, getCommandExecutionBuilder(), true);

	// Initialize command target resolver.
	init.addInitializeStep(this, getCommandTargetResolver(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#start(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
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
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#stop(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
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

    private IDeviceManagement getDeviceManagement() {
	return ((ICommandDeliveryMicroservice) getMicroservice()).getDeviceManagement();
    }

    private IOutboundCommandRouter getOutboundCommandRouter() {
	return ((ICommandDeliveryTenantEngine) getTenantEngine()).getOutboundCommandRouter();
    }

    private IUndeliveredCommandInvocationsProducer getUndeliveredCommandInvocationsProducer() {
	return ((ICommandDeliveryTenantEngine) getTenantEngine()).getUndeliveredCommandInvocationsProducer();
    }
}