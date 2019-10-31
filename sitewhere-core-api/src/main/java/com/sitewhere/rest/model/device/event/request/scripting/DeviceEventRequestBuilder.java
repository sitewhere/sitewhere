/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.request.scripting;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementCreateRequest;
import com.sitewhere.rest.model.device.event.scripting.DeviceEventSupport;
import com.sitewhere.rest.model.search.device.DeviceCommandSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Exposes builders for creating SiteWhere events.
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

    public DeviceMeasurementCreateRequest.Builder newMeasurements() {
	return new DeviceMeasurementCreateRequest.Builder();
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
	    IDeviceType type = deviceManagement.getDeviceType(targetAssignment.getDeviceTypeId());
	    DeviceCommandSearchCriteria criteria = new DeviceCommandSearchCriteria(1, 0);
	    criteria.setDeviceTypeToken(type.getToken());
	    ISearchResults<IDeviceCommand> commands = deviceManagement.listDeviceCommands(criteria);
	    IDeviceCommand match = null;
	    for (IDeviceCommand command : commands.getResults()) {
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

    public IDeviceEventManagement getEventManagement() {
	return eventManagement;
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

	/**
	 * Persist a single location event.
	 * 
	 * @param builder
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceLocation persist(DeviceLocationCreateRequest.Builder builder) throws SiteWhereException {
	    DeviceLocationCreateRequest request = builder.build();
	    return events.addDeviceLocations(getDeviceAssignment().getId(), request).get(0);
	}

	/**
	 * Persist multiple location events.
	 * 
	 * @param builders
	 * @return
	 * @throws SiteWhereException
	 */
	public List<IDeviceLocation> persistLocations(List<DeviceLocationCreateRequest.Builder> builders)
		throws SiteWhereException {
	    List<DeviceLocationCreateRequest> requests = new ArrayList<>();
	    for (DeviceLocationCreateRequest.Builder builder : builders) {
		DeviceLocationCreateRequest request = builder.build();
		requests.add(request);
	    }
	    return events.addDeviceLocations(getDeviceAssignment().getId(),
		    requests.toArray(new DeviceLocationCreateRequest[0]));
	}

	/**
	 * Persist a single measurement event.
	 * 
	 * @param builder
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceMeasurement persist(DeviceMeasurementCreateRequest.Builder builder) throws SiteWhereException {
	    DeviceMeasurementCreateRequest request = builder.build();
	    return events.addDeviceMeasurements(getDeviceAssignment().getId(), request).get(0);
	}

	/**
	 * Persist multiple measurement events.
	 * 
	 * @param builders
	 * @return
	 * @throws SiteWhereException
	 */
	public List<IDeviceMeasurement> persistMeasurements(List<DeviceMeasurementCreateRequest.Builder> builders)
		throws SiteWhereException {
	    List<DeviceMeasurementCreateRequest> requests = new ArrayList<>();
	    for (DeviceMeasurementCreateRequest.Builder builder : builders) {
		DeviceMeasurementCreateRequest request = builder.build();
		requests.add(request);
	    }
	    return events.addDeviceMeasurements(getDeviceAssignment().getId(),
		    requests.toArray(new DeviceMeasurementCreateRequest[0]));
	}

	/**
	 * Persist a single alert event.
	 * 
	 * @param builder
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceAlert persist(DeviceAlertCreateRequest.Builder builder) throws SiteWhereException {
	    DeviceAlertCreateRequest request = builder.build();
	    return events.addDeviceAlerts(getDeviceAssignment().getId(), request).get(0);
	}

	/**
	 * Persist multiple alert events.
	 * 
	 * @param builders
	 * @return
	 * @throws SiteWhereException
	 */
	public List<IDeviceAlert> persistAlerts(List<DeviceAlertCreateRequest.Builder> builders)
		throws SiteWhereException {
	    List<DeviceAlertCreateRequest> requests = new ArrayList<>();
	    for (DeviceAlertCreateRequest.Builder builder : builders) {
		DeviceAlertCreateRequest request = builder.build();
		requests.add(request);
	    }
	    return events.addDeviceAlerts(getDeviceAssignment().getId(),
		    requests.toArray(new DeviceAlertCreateRequest[0]));
	}

	public IDeviceCommandInvocation persist(DeviceCommandInvocationCreateRequest.Builder builder)
		throws SiteWhereException {
	    DeviceCommandInvocationCreateRequest request = builder.build();
	    return events.addDeviceCommandInvocations(getDeviceAssignment().getId(), request).get(0);
	}

	public IDeviceAssignment getDeviceAssignment() {
	    return deviceAssignment;
	}
    }
}