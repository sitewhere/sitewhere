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
package com.sitewhere.event.persistence;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.persistence.Persistence;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.rest.model.device.event.DeviceEventBatchResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.rest.model.device.streaming.DeviceStreamData;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.AlertSource;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.streaming.request.IDeviceStreamDataCreateRequest;

/**
 * Common methods needed by device service provider implementations.
 */
public class DeviceEventManagementPersistence extends Persistence {

    /**
     * Executes logic to process a batch of device events.
     * 
     * @param context
     * @param batch
     * @param eventManagement
     * @return
     * @throws SiteWhereException
     */
    public static DeviceEventBatchResponse deviceEventBatchLogic(IDeviceEventContext context, IDeviceEventBatch batch,
	    IDeviceEventManagement eventManagement) throws SiteWhereException {
	DeviceEventBatchResponse response = new DeviceEventBatchResponse();
	for (IDeviceMeasurementCreateRequest mx : batch.getMeasurements()) {
	    response.getCreatedMeasurements().addAll(eventManagement.addDeviceMeasurements(context, mx));
	}
	for (IDeviceLocationCreateRequest location : batch.getLocations()) {
	    response.getCreatedLocations().addAll(eventManagement.addDeviceLocations(context, location));
	}
	for (IDeviceAlertCreateRequest alert : batch.getAlerts()) {
	    response.getCreatedAlerts().addAll(eventManagement.addDeviceAlerts(context, alert));
	}
	return response;
    }

    /**
     * Common creation logic for all device events.
     * 
     * @param request
     * @param context
     * @param target
     * @throws SiteWhereException
     */
    public static void deviceEventCreateLogic(IDeviceEventCreateRequest request, IDeviceEventContext context,
	    DeviceEvent target) throws SiteWhereException {
	target.setId(UUID.randomUUID());
	target.setAlternateId(request.getAlternateId());
	target.setDeviceId(context.getDeviceId());
	target.setDeviceAssignmentId(context.getDeviceAssignmentId());
	target.setCustomerId(context.getCustomerId());
	target.setAreaId(context.getAreaId());
	target.setAssetId(context.getAssetId());
	if (request.getEventDate() != null) {
	    target.setEventDate(request.getEventDate());
	} else {
	    target.setEventDate(new Date());
	}
	target.setReceivedDate(new Date());
	MetadataProvider.copy(request.getMetadata(), target);
    }

    /**
     * Common logic for creating {@link DeviceMeasurement} from
     * {@link IDeviceMeasurementCreateRequest}.
     * 
     * @param context
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceMeasurement deviceMeasurementCreateLogic(IDeviceEventContext context,
	    IDeviceMeasurementCreateRequest request) throws SiteWhereException {
	DeviceMeasurement measurements = new DeviceMeasurement();
	deviceEventCreateLogic(request, context, measurements);
	measurements.setName(request.getName());
	measurements.setValue(request.getValue());
	return measurements;
    }

    /**
     * Common logic for creating {@link DeviceLocation} from
     * {@link IDeviceLocationCreateRequest}.
     * 
     * @param context
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceLocation deviceLocationCreateLogic(IDeviceEventContext context,
	    IDeviceLocationCreateRequest request) throws SiteWhereException {
	DeviceLocation location = new DeviceLocation();
	deviceEventCreateLogic(request, context, location);
	location.setLatitude(request.getLatitude());
	location.setLongitude(request.getLongitude());
	location.setElevation(request.getElevation());
	return location;
    }

    /**
     * Common logic for creating {@link DeviceAlert} from
     * {@link IDeviceAlertCreateRequest}.
     * 
     * @param context
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAlert deviceAlertCreateLogic(IDeviceEventContext context, IDeviceAlertCreateRequest request)
	    throws SiteWhereException {
	DeviceAlert alert = new DeviceAlert();
	deviceEventCreateLogic(request, context, alert);

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
     * @param context
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStreamData deviceStreamDataCreateLogic(IDeviceEventContext context,
	    IDeviceStreamDataCreateRequest request) throws SiteWhereException {
	DeviceStreamData streamData = new DeviceStreamData();
	deviceEventCreateLogic(request, context, streamData);

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
     * @param context
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommandInvocation deviceCommandInvocationCreateLogic(IDeviceEventContext context,
	    IDeviceCommand command, IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
	requireNotNull("Initiator", request.getInitiator());
	requireNotNull("Target", request.getTarget());

	DeviceCommandInvocation ci = new DeviceCommandInvocation();
	deviceEventCreateLogic(request, context, ci);
	ci.setDeviceCommandId(command.getId());
	ci.setInitiator(request.getInitiator());
	ci.setInitiatorId(request.getInitiatorId());
	ci.setTarget(request.getTarget());
	ci.setTargetId(request.getTargetId());
	ci.setParameterValues(request.getParameterValues());
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
     * @param context
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommandResponse deviceCommandResponseCreateLogic(IDeviceEventContext context,
	    IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
	requireNotNull("Originating Event Id", request.getOriginatingEventId());

	DeviceCommandResponse response = new DeviceCommandResponse();
	deviceEventCreateLogic(request, context, response);
	response.setOriginatingEventId(request.getOriginatingEventId());
	response.setResponseEventId(request.getResponseEventId());
	response.setResponse(request.getResponse());
	return response;
    }

    /**
     * Common logic for creating a {@link DeviceStateChange} from an
     * {@link IDeviceStateChangeCreateRequest}.
     * 
     * @param context
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStateChange deviceStateChangeCreateLogic(IDeviceEventContext context,
	    IDeviceStateChangeCreateRequest request) throws SiteWhereException {
	require("Attribute", request.getAttribute());
	require("Type", request.getType());

	DeviceStateChange state = new DeviceStateChange();
	deviceEventCreateLogic(request, context, state);
	state.setAttribute(request.getAttribute());
	state.setType(request.getType());
	state.setPreviousState(request.getPreviousState());
	state.setNewState(request.getNewState());

	return state;
    }
}