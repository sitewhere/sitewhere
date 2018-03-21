/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.persistence.Persistence;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.rest.model.device.event.DeviceEventBatchResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.rest.model.device.event.DeviceStreamData;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.AlertSource;
import com.sitewhere.spi.device.event.CommandStatus;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;

/**
 * Common methods needed by device service provider implementations.
 * 
 * @author Derek
 */
public class DeviceEventManagementPersistence extends Persistence {

    /**
     * Executes logic to process a batch of device events.
     * 
     * @param assignmentToken
     * @param batch
     * @param management
     * @return
     * @throws SiteWhereException
     */
    public static DeviceEventBatchResponse deviceEventBatchLogic(IDeviceAssignment assignment, IDeviceEventBatch batch,
	    IDeviceEventManagement management) throws SiteWhereException {
	DeviceEventBatchResponse response = new DeviceEventBatchResponse();
	for (IDeviceMeasurementsCreateRequest measurements : batch.getMeasurements()) {
	    response.getCreatedMeasurements().add(management.addDeviceMeasurements(assignment, measurements));
	}
	for (IDeviceLocationCreateRequest location : batch.getLocations()) {
	    response.getCreatedLocations().add(management.addDeviceLocation(assignment, location));
	}
	for (IDeviceAlertCreateRequest alert : batch.getAlerts()) {
	    response.getCreatedAlerts().add(management.addDeviceAlert(assignment, alert));
	}
	return response;
    }

    /**
     * Common creation logic for all device events.
     * 
     * @param request
     * @param assignment
     * @param target
     * @throws SiteWhereException
     */
    public static void deviceEventCreateLogic(IDeviceEventCreateRequest request, IDeviceAssignment assignment,
	    DeviceEvent target) throws SiteWhereException {
	target.setId(UUID.randomUUID());
	target.setAlternateId(request.getAlternateId());
	target.setDeviceId(assignment.getDeviceId());
	target.setDeviceAssignmentId(assignment.getId());
	target.setAreaId(assignment.getAreaId());
	target.setAssetId(assignment.getAssetId());
	if (request.getEventDate() != null) {
	    target.setEventDate(request.getEventDate());
	} else {
	    target.setEventDate(new Date());
	}
	target.setReceivedDate(new Date());
	MetadataProvider.copy(request.getMetadata(), target);
    }

    /**
     * Common logic for updating a device event (Only metadata can be updated).
     * 
     * @param metadata
     * @param target
     * @throws SiteWhereException
     */
    public static void deviceEventUpdateLogic(IDeviceEventCreateRequest request, DeviceEvent target)
	    throws SiteWhereException {
	if (request.getMetadata() != null) {
	    target.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), target);
	}
    }

    /**
     * Common logic for creating {@link DeviceMeasurements} from
     * {@link IDeviceMeasurementsCreateRequest}.
     * 
     * @param request
     * @param assignment
     * @return
     * @throws SiteWhereException
     */
    public static DeviceMeasurements deviceMeasurementsCreateLogic(IDeviceMeasurementsCreateRequest request,
	    IDeviceAssignment assignment) throws SiteWhereException {
	DeviceMeasurements measurements = new DeviceMeasurements();
	deviceEventCreateLogic(request, assignment, measurements);
	for (String key : request.getMeasurements().keySet()) {
	    measurements.addOrReplaceMeasurement(key, request.getMeasurement(key));
	}
	return measurements;
    }

    /**
     * Common logic for creating {@link DeviceLocation} from
     * {@link IDeviceLocationCreateRequest}.
     * 
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceLocation deviceLocationCreateLogic(IDeviceAssignment assignment,
	    IDeviceLocationCreateRequest request) throws SiteWhereException {
	DeviceLocation location = new DeviceLocation();
	deviceEventCreateLogic(request, assignment, location);
	location.setLatitude(request.getLatitude());
	location.setLongitude(request.getLongitude());
	location.setElevation(request.getElevation());
	return location;
    }

    /**
     * Common logic for creating {@link DeviceAlert} from
     * {@link IDeviceAlertCreateRequest}.
     * 
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAlert deviceAlertCreateLogic(IDeviceAssignment assignment, IDeviceAlertCreateRequest request)
	    throws SiteWhereException {
	DeviceAlert alert = new DeviceAlert();
	deviceEventCreateLogic(request, assignment, alert);

	if (request.getSource() != null) {
	    alert.setSource(request.getSource());
	} else {
	    alert.setSource(AlertSource.Device);
	}

	if (request.getLevel() != null) {
	    alert.setLevel(request.getLevel());
	} else {
	    alert.setLevel(AlertLevel.Info);
	}

	alert.setType(request.getType());
	alert.setMessage(request.getMessage());
	return alert;
    }

    /**
     * Common logic for creating {@link DeviceStreamData} from
     * {@link IDeviceStreamDataCreateRequest}.
     * 
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStreamData deviceStreamDataCreateLogic(IDeviceAssignment assignment,
	    IDeviceStreamDataCreateRequest request) throws SiteWhereException {
	DeviceStreamData streamData = new DeviceStreamData();
	deviceEventCreateLogic(request, assignment, streamData);

	require("Stream Id", request.getStreamId());
	streamData.setStreamId(request.getStreamId());

	requireNotNull("Sequence Number", request.getSequenceNumber());
	streamData.setSequenceNumber(request.getSequenceNumber());

	streamData.setData(request.getData());
	return streamData;
    }

    /**
     * Common logic for creating {@link DeviceCommandInvocation} from an
     * {@link IDeviceCommandInvocationCreateRequest}.
     * 
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommandInvocation deviceCommandInvocationCreateLogic(IDeviceAssignment assignment,
	    IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
	requireNotNull("Initiator", request.getInitiator());
	requireNotNull("Target", request.getTarget());

	DeviceCommandInvocation ci = new DeviceCommandInvocation();
	deviceEventCreateLogic(request, assignment, ci);
	ci.setCommandToken(request.getCommandToken());
	ci.setInitiator(request.getInitiator());
	ci.setInitiatorId(request.getInitiatorId());
	ci.setTarget(request.getTarget());
	ci.setTargetId(request.getTargetId());
	ci.setParameterValues(request.getParameterValues());
	if (request.getStatus() != null) {
	    ci.setStatus(request.getStatus());
	} else {
	    ci.setStatus(CommandStatus.Pending);
	}
	return ci;
    }

    /**
     * Verify that data supplied for command parameters is valid.
     * 
     * @param parameter
     * @param values
     * @throws SiteWhereException
     */
    protected static void checkParameter(ICommandParameter parameter, Map<String, String> values)
	    throws SiteWhereException {

	String value = values.get(parameter.getName());
	boolean parameterValueIsNull = (value == null);
	boolean parameterValueIsEmpty = true;

	if (!parameterValueIsNull) {
	    value = value.trim();
	    parameterValueIsEmpty = value.length() == 0;
	}

	// Handle the required parameters first
	if (parameter.isRequired()) {
	    if (parameterValueIsNull) {
		throw new SiteWhereException("Required parameter '" + parameter.getName() + "' is missing.");
	    }

	    if (parameterValueIsEmpty) {
		throw new SiteWhereException(
			"Required parameter '" + parameter.getName() + "' has no value assigned to it.");
	    }
	} else if (parameterValueIsNull || parameterValueIsEmpty) {
	    return;
	}

	switch (parameter.getType()) {
	case Fixed32:
	case Fixed64:
	case Int32:
	case Int64:
	case SFixed32:
	case SFixed64:
	case SInt32:
	case SInt64:
	case UInt32:
	case UInt64: {
	    try {
		Long.parseLong(value);
	    } catch (NumberFormatException e) {
		throw new SiteWhereException("Parameter '" + parameter.getName() + "' must be integral.");
	    }
	}
	case Float: {
	    try {
		Float.parseFloat(value);
	    } catch (NumberFormatException e) {
		throw new SiteWhereException("Parameter '" + parameter.getName() + "' must be a float.");
	    }
	}
	case Double: {
	    try {
		Double.parseDouble(value);
	    } catch (NumberFormatException e) {
		throw new SiteWhereException("Parameter '" + parameter.getName() + "' must be a double.");
	    }
	}
	default:
	}
    }

    /**
     * Common logic for creating a {@link DeviceCommandResponse} from an
     * {@link IDeviceCommandResponseCreateRequest}.
     * 
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommandResponse deviceCommandResponseCreateLogic(IDeviceAssignment assignment,
	    IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
	if (request.getOriginatingEventId() == null) {
	    throw new SiteWhereSystemException(ErrorCode.IncompleteData, ErrorLevel.ERROR);
	}
	DeviceCommandResponse response = new DeviceCommandResponse();
	deviceEventCreateLogic(request, assignment, response);
	response.setOriginatingEventId(request.getOriginatingEventId());
	response.setResponseEventId(request.getResponseEventId());
	response.setResponse(request.getResponse());
	return response;
    }

    /**
     * Common logic for creating a {@link DeviceStateChange} from an
     * {@link IDeviceStateChangeCreateRequest}.
     * 
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStateChange deviceStateChangeCreateLogic(IDeviceAssignment assignment,
	    IDeviceStateChangeCreateRequest request) throws SiteWhereException {
	if (request.getCategory() == null) {
	    throw new SiteWhereSystemException(ErrorCode.IncompleteData, ErrorLevel.ERROR);
	}
	if (request.getType() == null) {
	    throw new SiteWhereSystemException(ErrorCode.IncompleteData, ErrorLevel.ERROR);
	}
	DeviceStateChange state = new DeviceStateChange();
	deviceEventCreateLogic(request, assignment, state);
	state.setCategory(request.getCategory());
	state.setType(request.getType());
	state.setPreviousState(request.getPreviousState());
	state.setNewState(request.getNewState());
	if (request.getData() != null) {
	    state.getData().putAll(request.getData());
	}
	return state;
    }
}