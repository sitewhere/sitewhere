/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.request.scripting;

import java.util.List;

import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.event.scripting.DeviceEventSupport;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventManagement;

/**
 * Exposes builders for creating SiteWhere events.
 * 
 * @author Derek
 */
public class DeviceEventRequestBuilder {

    /** Device management implementation */
    private IDeviceManagement deviceManagement;

    /** Event management interface */
    private IDeviceEventManagement eventManagement;

    public DeviceEventRequestBuilder(IDeviceManagement deviceManagement, IDeviceEventManagement eventManagement) {
	this.deviceManagement = deviceManagement;
	this.eventManagement = eventManagement;
    }

    public DeviceLocationCreateRequest.Builder newLocation(double latitude, double longitude) {
	return new DeviceLocationCreateRequest.Builder(latitude, longitude);
    }

    public DeviceMeasurementsCreateRequest.Builder newMeasurements() {
	return new DeviceMeasurementsCreateRequest.Builder();
    }

    public DeviceAlertCreateRequest.Builder newAlert(String type, String message) {
	return new DeviceAlertCreateRequest.Builder(type, message);
    }

    public DeviceCommandInvocationCreateRequest.Builder newCommandInvocation(String commandName, String target) {
	try {
	    IDeviceAssignment targetAssignment = deviceManagement.getDeviceAssignmentByToken(target);
	    if (targetAssignment == null) {
		throw new SiteWhereException("Target assignment not found: " + target);
	    }
	    IDevice targetDevice = deviceManagement.getDevice(targetAssignment.getDeviceId());
	    List<IDeviceCommand> commands = deviceManagement.listDeviceCommands(targetDevice.getDeviceSpecificationId(),
		    false);
	    IDeviceCommand match = null;
	    for (IDeviceCommand command : commands) {
		if (command.getName().equals(commandName)) {
		    match = command;
		}
	    }
	    if (match == null) {
		throw new SiteWhereException("Command not executed. No command found matching: " + commandName);
	    }
	    return new DeviceCommandInvocationCreateRequest.Builder(match.getToken(), target);
	} catch (SiteWhereException e) {
	    throw new RuntimeException(e);
	}
    }

    public AssignmentScope forSameAssignmentAs(DeviceEventSupport support) {
	return new AssignmentScope(getEventManagement(), support.getDeviceAssignment());
    }

    public AssignmentScope forSameAssignmentAs(IDeviceEvent event) throws SiteWhereException {
	return new AssignmentScope(getEventManagement(),
		getDeviceManagement().getDeviceAssignment(event.getDeviceAssignmentId()));
    }

    public AssignmentScope forAssignment(String assignmentToken) throws SiteWhereException {
	return new AssignmentScope(getEventManagement(),
		getDeviceManagement().getDeviceAssignmentByToken(assignmentToken));
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    public IDeviceEventManagement getEventManagement() {
	return eventManagement;
    }

    public void setEventManagement(IDeviceEventManagement eventManagement) {
	this.eventManagement = eventManagement;
    }

    public static class AssignmentScope {

	/** Event management interface */
	private IDeviceEventManagement events;

	/** Device assignment */
	private IDeviceAssignment deviceAssignment;

	public AssignmentScope(IDeviceEventManagement events, IDeviceAssignment deviceAssignment) {
	    this.events = events;
	    this.deviceAssignment = deviceAssignment;
	}

	public AssignmentScope persist(DeviceLocationCreateRequest.Builder builder) throws SiteWhereException {
	    DeviceLocationCreateRequest request = builder.build();
	    events.addDeviceLocation(getDeviceAssignment(), request);
	    return this;
	}

	public AssignmentScope persist(DeviceMeasurementsCreateRequest.Builder builder) throws SiteWhereException {
	    DeviceMeasurementsCreateRequest request = builder.build();
	    events.addDeviceMeasurements(getDeviceAssignment(), request);
	    return this;
	}

	public AssignmentScope persist(DeviceAlertCreateRequest.Builder builder) throws SiteWhereException {
	    DeviceAlertCreateRequest request = builder.build();
	    events.addDeviceAlert(getDeviceAssignment(), request);
	    return this;
	}

	public AssignmentScope persist(DeviceCommandInvocationCreateRequest.Builder builder) throws SiteWhereException {
	    DeviceCommandInvocationCreateRequest request = builder.build();
	    events.addDeviceCommandInvocation(getDeviceAssignment(), request);
	    return this;
	}

	public IDeviceAssignment getDeviceAssignment() {
	    return deviceAssignment;
	}

	public void setDeviceAssignment(IDeviceAssignment deviceAssignment) {
	    this.deviceAssignment = deviceAssignment;
	}
    }
}