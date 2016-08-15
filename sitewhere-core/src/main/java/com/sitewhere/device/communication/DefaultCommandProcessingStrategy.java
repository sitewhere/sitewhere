/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.device.communication.ICommandExecutionBuilder;
import com.sitewhere.spi.device.communication.ICommandProcessingStrategy;
import com.sitewhere.spi.device.communication.ICommandTargetResolver;
import com.sitewhere.spi.device.communication.IDeviceCommunication;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Default implementation of {@link ICommandProcessingStrategy}.
 * 
 * @author Derek
 */
public class DefaultCommandProcessingStrategy extends TenantLifecycleComponent implements ICommandProcessingStrategy {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	/** Configured command target resolver */
	private ICommandTargetResolver commandTargetResolver = new DefaultCommandTargetResolver();

	/** Configured command execution builder */
	private ICommandExecutionBuilder commandExecutionBuilder = new DefaultCommandExecutionBuilder();

	public DefaultCommandProcessingStrategy() {
		super(LifecycleComponentType.CommandProcessingStrategy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.ICommandProcessingStrategy#
	 * deliverCommand
	 * (com.sitewhere.spi.device.communication.IDeviceCommunication,
	 * com.sitewhere.spi.device.event.IDeviceCommandInvocation)
	 */
	@Override
	public void deliverCommand(IDeviceCommunication communication, IDeviceCommandInvocation invocation)
			throws SiteWhereException {
		LOGGER.debug("Command processing strategy handling invocation.");
		IDeviceCommand command = SiteWhere.getServer().getDeviceManagement(getTenant())
				.getDeviceCommandByToken(invocation.getCommandToken());
		if (command != null) {
			IDeviceCommandExecution execution = getCommandExecutionBuilder().createExecution(command, invocation);
			List<IDeviceAssignment> assignments = getCommandTargetResolver().resolveTargets(invocation);
			for (IDeviceAssignment assignment : assignments) {
				IDevice device = SiteWhere.getServer().getDeviceManagement(getTenant())
						.getDeviceForAssignment(assignment);
				if (device == null) {
					throw new SiteWhereException("Targeted assignment references device that does not exist.");
				}

				IDeviceNestingContext nesting = NestedDeviceSupport.calculateNestedDeviceInformation(device,
						getTenant());
				communication.getOutboundCommandRouter().routeCommand(execution, nesting, assignment);
			}
		} else {
			throw new SiteWhereException("Invalid command referenced from invocation.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.ICommandProcessingStrategy#
	 * deliverSystemCommand
	 * (com.sitewhere.spi.device.communication.IDeviceCommunication,
	 * java.lang.String, com.sitewhere.spi.device.command.ISystemCommand)
	 */
	@Override
	public void deliverSystemCommand(IDeviceCommunication communication, String hardwareId, ISystemCommand command)
			throws SiteWhereException {
		IDeviceManagement management = SiteWhere.getServer().getDeviceManagement(getTenant());
		IDevice device = management.getDeviceByHardwareId(hardwareId);
		if (device == null) {
			throw new SiteWhereException("Targeted assignment references device that does not exist.");
		}
		IDeviceAssignment assignment = management.getCurrentDeviceAssignment(device);
		IDeviceNestingContext nesting = NestedDeviceSupport.calculateNestedDeviceInformation(device, getTenant());
		communication.getOutboundCommandRouter().routeSystemCommand(command, nesting, assignment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		getLifecycleComponents().clear();

		// Start command execution builder.
		if (getCommandExecutionBuilder() == null) {
			throw new SiteWhereException("No command execution builder configured for command processing.");
		}
		startNestedComponent(getCommandExecutionBuilder(), true);

		// Start command target resolver.
		if (getCommandTargetResolver() == null) {
			throw new SiteWhereException("No command target resolver configured for command processing.");
		}
		startNestedComponent(getCommandTargetResolver(), true);
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
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		// Stop command execution builder.
		if (getCommandExecutionBuilder() != null) {
			getCommandExecutionBuilder().lifecycleStop();
		}

		// Stop command target resolver.
		if (getCommandTargetResolver() != null) {
			getCommandTargetResolver().lifecycleStop();
		}
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
}