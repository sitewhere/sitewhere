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

import com.sitewhere.persistence.Persistence;
import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.rest.model.asset.AssetCategory;
import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.asset.LocationAsset;
import com.sitewhere.rest.model.asset.PersonAsset;
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
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest;
import com.sitewhere.spi.asset.request.IAssetCreateRequest;
import com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest;
import com.sitewhere.spi.asset.request.ILocationAssetCreateRequest;
import com.sitewhere.spi.asset.request.IPersonAssetCreateRequest;
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
	target.setAlternateId(request.getAlternateId());
	target.setSiteToken(assignment.getSiteToken());
	target.setDeviceAssignmentToken(assignment.getToken());
	target.setAssignmentType(assignment.getAssignmentType());
	target.setAssetReference(assignment.getAssetReference());
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

	require(request.getStreamId());
	streamData.setStreamId(request.getStreamId());

	requireNotNull(request.getSequenceNumber());
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
	requireNotNull(request.getInitiator());
	requireNotNull(request.getTarget());

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

    /**
     * Common logic for creating an asset category.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static AssetCategory assetCategoryCreateLogic(IAssetCategoryCreateRequest request)
	    throws SiteWhereException {
	AssetCategory category = new AssetCategory();

	require(request.getId());
	category.setId(request.getId());

	// Name is required.
	require(request.getName());
	category.setName(request.getName());

	// Type is required.
	requireNotNull(request.getAssetType());
	category.setAssetType(request.getAssetType());

	return category;
    }

    /**
     * Common logic for updating an existing asset category.
     * 
     * @param request
     * @param existing
     * @return
     * @throws SiteWhereException
     */
    public static AssetCategory assetCategoryUpdateLogic(IAssetCategoryCreateRequest request, AssetCategory existing)
	    throws SiteWhereException {
	if (!request.getId().equals(existing.getId())) {
	    throw new SiteWhereException("Can not change the id of an existing asset category.");
	}

	if (request.getAssetType() != existing.getAssetType()) {
	    throw new SiteWhereException("Can not change the asset type of an existing asset category.");
	}

	if (request.getName() != null) {
	    existing.setName(request.getName());
	}

	return existing;
    }

    /**
     * Handle base logic common to all asset types.
     * 
     * @param category
     * @param request
     * @param asset
     * @throws SiteWhereException
     */
    public static void assetCreateLogic(IAssetCategory category, IAssetCreateRequest request, Asset asset)
	    throws SiteWhereException {
	asset.setType(category.getAssetType());

	require(category.getId());
	asset.setAssetCategoryId(category.getId());

	require(request.getId());
	asset.setId(request.getId());

	require(request.getName());
	asset.setName(request.getName());

	require(request.getImageUrl());
	asset.setImageUrl(request.getImageUrl());

	asset.getProperties().putAll(request.getProperties());
    }

    /**
     * Common logic for updating assets.
     * 
     * @param asset
     * @param request
     * @throws SiteWhereException
     */
    public static void assetUpdateLogic(Asset asset, IAssetCreateRequest request) throws SiteWhereException {
	if (!asset.getId().equals(request.getId())) {
	    throw new SiteWhereException("Asset id can not be changed.");
	}

	if (request.getName() != null) {
	    asset.setName(request.getName());
	}
	if (request.getImageUrl() != null) {
	    asset.setImageUrl(request.getImageUrl());
	}
	if (request.getProperties() != null) {
	    asset.getProperties().clear();
	    asset.getProperties().putAll(request.getProperties());
	}
    }

    /**
     * Handle common logic for creating a person asset.
     * 
     * @param category
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static PersonAsset personAssetCreateLogic(IAssetCategory category, IPersonAssetCreateRequest request)
	    throws SiteWhereException {
	if (category.getAssetType() != AssetType.Person) {
	    throw new SiteWhereSystemException(ErrorCode.AssetTypeNotAllowed, ErrorLevel.ERROR);
	}

	PersonAsset person = new PersonAsset();
	assetCreateLogic(category, request, person);

	person.setUserName(request.getUserName());
	person.setEmailAddress(request.getEmailAddress());
	person.getRoles().addAll(request.getRoles());

	return person;
    }

    /**
     * Handle common logic for updating a person asset.
     * 
     * @param person
     * @param request
     * @throws SiteWhereException
     */
    public static void personAssetUpdateLogic(PersonAsset person, IPersonAssetCreateRequest request)
	    throws SiteWhereException {
	assetUpdateLogic(person, request);

	if (request.getUserName() != null) {
	    person.setUserName(request.getUserName());
	}
	if (request.getEmailAddress() != null) {
	    person.setEmailAddress(request.getEmailAddress());
	}
	if (request.getRoles() != null) {
	    person.getRoles().clear();
	    person.getRoles().addAll(request.getRoles());
	}
    }

    /**
     * Handle common logic for creating a hardware asset.
     * 
     * @param category
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static HardwareAsset hardwareAssetCreateLogic(IAssetCategory category, IHardwareAssetCreateRequest request)
	    throws SiteWhereException {
	if ((category.getAssetType() != AssetType.Hardware) && (category.getAssetType() != AssetType.Device)) {
	    throw new SiteWhereSystemException(ErrorCode.AssetTypeNotAllowed, ErrorLevel.ERROR);
	}

	HardwareAsset hardware = new HardwareAsset();
	assetCreateLogic(category, request, hardware);

	hardware.setSku(request.getSku());
	hardware.setDescription(request.getDescription());

	return hardware;
    }

    /**
     * Handle common logic for updating a hardware asset.
     * 
     * @param hardware
     * @param request
     * @throws SiteWhereException
     */
    public static void hardwareAssetUpdateLogic(HardwareAsset hardware, IHardwareAssetCreateRequest request)
	    throws SiteWhereException {
	assetUpdateLogic(hardware, request);

	if (request.getSku() != null) {
	    hardware.setSku(request.getSku());
	}
	if (request.getDescription() != null) {
	    hardware.setDescription(request.getDescription());
	}
    }

    /**
     * Handle common logic for creating a location asset.
     * 
     * @param category
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static LocationAsset locationAssetCreateLogic(IAssetCategory category, ILocationAssetCreateRequest request)
	    throws SiteWhereException {
	if (category.getAssetType() != AssetType.Location) {
	    throw new SiteWhereSystemException(ErrorCode.AssetTypeNotAllowed, ErrorLevel.ERROR);
	}

	LocationAsset loc = new LocationAsset();
	assetCreateLogic(category, request, loc);

	loc.setLatitude(request.getLatitude());
	loc.setLongitude(request.getLongitude());
	loc.setElevation(request.getElevation());

	return loc;
    }

    /**
     * Handle common logic for updating a location asset.
     * 
     * @param location
     * @param request
     * @throws SiteWhereException
     */
    public static void locationAssetUpdateLogic(LocationAsset location, ILocationAssetCreateRequest request)
	    throws SiteWhereException {
	assetUpdateLogic(location, request);

	if (request.getLatitude() != null) {
	    location.setLatitude(request.getLatitude());
	}
	if (request.getLongitude() != null) {
	    location.setLongitude(request.getLongitude());
	}
	if (request.getElevation() != null) {
	    location.setElevation(request.getElevation());
	}
    }
}