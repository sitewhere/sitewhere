/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device;

import java.util.Date;
import java.util.Map;

import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.search.device.DeviceCommandSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceActions;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Handles underlying logic to make common actions simpler to invoke from
 * scripts.
 */
public class DeviceActions implements IDeviceActions {

    /** Device management implementation */
    private IDeviceManagement deviceManagement;

    /** Device event management implementation */
    private IDeviceEventManagement deviceEventManagement;

    public DeviceActions(IDeviceManagement deviceManagement, IDeviceEventManagement deviceEventManagement) {
	this.deviceManagement = deviceManagement;
	this.deviceEventManagement = deviceEventManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceActions#createLocation(com.sitewhere.spi.
     * device.IDeviceAssignment, double, double, double, boolean)
     */
    @Override
    public void createLocation(IDeviceAssignment assignment, double latitude, double longitude, double elevation,
	    boolean updateState) throws SiteWhereException {
	DeviceLocationCreateRequest location = new DeviceLocationCreateRequest();
	location.setLatitude(latitude);
	location.setLongitude(longitude);
	location.setElevation(elevation);
	location.setEventDate(new Date());
	location.setUpdateState(updateState);
	getDeviceEventManagement().addDeviceLocations(assignment.getId(), location);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceActions#sendCommand(com.sitewhere.spi.
     * device.IDeviceAssignment, java.lang.String, java.util.Map)
     */
    @Override
    public void sendCommand(IDeviceAssignment assignment, String commandName, Map<String, String> parameters)
	    throws SiteWhereException {
	IDeviceType type = getDeviceManagement().getDeviceType(assignment.getDeviceTypeId());
	DeviceCommandSearchCriteria criteria = new DeviceCommandSearchCriteria(1, 0);
	criteria.setDeviceTypeToken(type.getToken());
	ISearchResults<IDeviceCommand> commands = getDeviceManagement().listDeviceCommands(criteria);
	IDeviceCommand match = null;
	for (IDeviceCommand command : commands.getResults()) {
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
	getDeviceEventManagement().addDeviceCommandInvocations(assignment.getId(), create);
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    public IDeviceEventManagement getDeviceEventManagement() {
	return deviceEventManagement;
    }

    public void setDeviceEventManagement(IDeviceEventManagement deviceEventManagement) {
	this.deviceEventManagement = deviceEventManagement;
    }
}