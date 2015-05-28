/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceActions;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandTarget;

/**
 * Handles underlying logic to make common actions simpler to invoke from scripts.
 * 
 * @author Derek
 */
public class DeviceActions implements IDeviceActions {

	/** Device management implementation */
	private IDeviceManagement deviceManagement;

	public DeviceActions(IDeviceManagement deviceManagement) {
		this.deviceManagement = deviceManagement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceActions#sendCommand(java.lang.String,
	 * java.lang.String, java.util.Map)
	 */
	@Override
	public void sendCommand(String assignmentToken, String commandName, Map<String, String> parameters)
			throws SiteWhereException {
		IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(assignmentToken);
		if (assignment == null) {
			throw new SiteWhereException("Command not executed. Assignment not found: " + assignmentToken);
		}
		IDevice device = getDeviceManagement().getDeviceForAssignment(assignment);
		List<IDeviceCommand> commands =
				getDeviceManagement().listDeviceCommands(device.getSpecificationToken(), false);
		IDeviceCommand match = null;
		for (IDeviceCommand command : commands) {
			if (command.getName().equals(commandName)) {
				match = command;
			}
		}
		if (match == null) {
			throw new SiteWhereException("Command not executed. No command found matching: " + commandName);
		}
		DeviceCommandInvocationCreateRequest create = new DeviceCommandInvocationCreateRequest();
		create.setCommandToken(match.getToken());
		create.setParameterValues(parameters);
		create.setInitiator(CommandInitiator.Script);
		create.setTarget(CommandTarget.Assignment);
		create.setTargetId(assignment.getToken());
		create.setEventDate(new Date());
		getDeviceManagement().addDeviceCommandInvocation(assignment.getToken(), match, create);
	}

	public IDeviceManagement getDeviceManagement() {
		return deviceManagement;
	}

	public void setDeviceManagement(IDeviceManagement deviceManagement) {
		this.deviceManagement = deviceManagement;
	}
}