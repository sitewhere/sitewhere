/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sitewhere.persistence.Persistence;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceElementMapping;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.DeviceStatus;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.SiteMapData;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.element.DeviceElementSchema;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.common.ILocation;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.element.IDeviceElementSchema;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCommandCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest;
import com.sitewhere.spi.device.request.IDeviceStatusCreateRequest;
import com.sitewhere.spi.device.request.ISiteCreateRequest;
import com.sitewhere.spi.device.request.IZoneCreateRequest;
import com.sitewhere.spi.device.util.DeviceSpecificationUtils;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;

/**
 * Common methods needed by device service provider implementations.
 * 
 * @author Derek
 */
public class DeviceManagementPersistence extends Persistence {

    /** Regular expression used to validate hardware ids */
    private static final Pattern HARDWARE_ID_REGEX = Pattern.compile("^[\\w-]+$");

    /**
     * Common logic for creating new device specification and populating it from
     * request.
     * 
     * @param request
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public static DeviceSpecification deviceSpecificationCreateLogic(IDeviceSpecificationCreateRequest request,
	    String token) throws SiteWhereException {
	DeviceSpecification spec = new DeviceSpecification();
	spec.setId(UUID.randomUUID());

	// Unique token is required.
	require(token);
	spec.setToken(token);

	// Name is required.
	require(request.getName());
	spec.setName(request.getName());

	// Asset reference is required.
	requireNotNull(request.getAssetReference());
	spec.setAssetReference(request.getAssetReference());

	// Container policy is required.
	requireNotNull(request.getContainerPolicy());
	spec.setContainerPolicy(request.getContainerPolicy());

	// If composite container policy and no device element schema, create
	// empty schema.
	if (request.getContainerPolicy() == DeviceContainerPolicy.Composite) {
	    IDeviceElementSchema schema = request.getDeviceElementSchema();
	    if (schema == null) {
		schema = new DeviceElementSchema();
	    }
	    spec.setDeviceElementSchema((DeviceElementSchema) schema);
	}

	MetadataProvider.copy(request.getMetadata(), spec);
	DeviceManagementPersistence.initializeEntityMetadata(spec);
	return spec;
    }

    /**
     * Common logic for updating a device specification from request.
     * 
     * @param request
     * @param target
     * @throws SiteWhereException
     */
    public static void deviceSpecificationUpdateLogic(IDeviceSpecificationCreateRequest request,
	    DeviceSpecification target) throws SiteWhereException {
	if (request.getName() != null) {
	    target.setName(request.getName());
	}
	if (request.getContainerPolicy() != null) {
	    target.setContainerPolicy(request.getContainerPolicy());
	}
	// Only allow schema to be set if new or existing container policy is
	// 'composite'.
	if (target.getContainerPolicy() == DeviceContainerPolicy.Composite) {
	    if (request.getContainerPolicy() == DeviceContainerPolicy.Standalone) {
		target.setDeviceElementSchema(null);
	    } else {
		IDeviceElementSchema schema = request.getDeviceElementSchema();
		if (schema == null) {
		    schema = new DeviceElementSchema();
		}
		target.setDeviceElementSchema((DeviceElementSchema) schema);
	    }
	}
	if (request.getAssetReference() != null) {
	    target.setAssetReference(request.getAssetReference());
	}
	if (request.getMetadata() != null) {
	    target.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), target);
	}
	DeviceManagementPersistence.setUpdatedEntityMetadata(target);
    }

    /**
     * Common logic for creating new device command and populating it from request.
     * 
     * @param spec
     * @param request
     * @param token
     * @param existing
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommand deviceCommandCreateLogic(IDeviceSpecification spec, IDeviceCommandCreateRequest request,
	    String token, List<IDeviceCommand> existing) throws SiteWhereException {
	DeviceCommand command = new DeviceCommand();
	command.setId(UUID.randomUUID());

	// Token is required.
	require(token);
	command.setToken(token);

	// Name is required.
	require(request.getName());
	command.setName(request.getName());

	command.setDeviceSpecificationId(spec.getId());
	command.setNamespace(request.getNamespace());
	command.setDescription(request.getDescription());
	command.getParameters().addAll(request.getParameters());

	checkDuplicateCommand(command, existing);

	MetadataProvider.copy(request.getMetadata(), command);
	DeviceManagementPersistence.initializeEntityMetadata(command);
	return command;
    }

    /**
     * Checks whether a command is already in the given list (same name and
     * namespace).
     * 
     * @param command
     * @param existing
     * @throws SiteWhereException
     */
    protected static void checkDuplicateCommand(DeviceCommand command, List<IDeviceCommand> existing)
	    throws SiteWhereException {
	boolean duplicate = false;
	for (IDeviceCommand current : existing) {
	    if (current.getName().equals(command.getName())) {
		if (current.getNamespace() == null) {
		    if (command.getNamespace() == null) {
			duplicate = true;
			break;
		    }
		} else if (current.getNamespace().equals(command.getNamespace())) {
		    duplicate = true;
		    break;
		}
	    }
	}
	if (duplicate) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceCommandExists, ErrorLevel.ERROR);
	}
    }

    /**
     * Common logic for updating a device command from request.
     * 
     * @param request
     * @param target
     * @param existing
     * @throws SiteWhereException
     */
    public static void deviceCommandUpdateLogic(IDeviceCommandCreateRequest request, DeviceCommand target,
	    List<IDeviceCommand> existing) throws SiteWhereException {
	if (request.getName() != null) {
	    target.setName(request.getName());
	}
	if (request.getNamespace() != null) {
	    target.setNamespace(request.getNamespace());
	}

	// Make sure the update will not result in a duplicate.
	checkDuplicateCommand(target, existing);

	if (request.getDescription() != null) {
	    target.setDescription(request.getDescription());
	}
	if (request.getParameters() != null) {
	    target.getParameters().clear();
	    target.getParameters().addAll(request.getParameters());
	}
	if (request.getMetadata() != null) {
	    target.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), target);
	}
	DeviceManagementPersistence.setUpdatedEntityMetadata(target);
    }

    /**
     * Common logic for creating new device status and populating it from request.
     * 
     * @param spec
     * @param request
     * @param existing
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStatus deviceStatusCreateLogic(IDeviceSpecification spec, IDeviceStatusCreateRequest request,
	    List<IDeviceStatus> existing) throws SiteWhereException {
	DeviceStatus status = new DeviceStatus();
	status.setId(UUID.randomUUID());

	// Code is required.
	require(request.getCode());
	status.setCode(request.getCode());

	// Name is required.
	require(request.getName());
	status.setName(request.getName());

	status.setDeviceSpecificationId(spec.getId());
	status.setBackgroundColor(request.getBackgroundColor());
	status.setForegroundColor(request.getForegroundColor());
	status.setBorderColor(request.getBorderColor());
	status.setIcon(request.getIcon());

	checkDuplicateStatus(status, existing);

	MetadataProvider.copy(request.getMetadata(), status);
	return status;
    }

    /**
     * Common logic for updating a device status from request.
     * 
     * @param request
     * @param target
     * @param existing
     * @throws SiteWhereException
     */
    public static void deviceStatusUpdateLogic(IDeviceStatusCreateRequest request, DeviceStatus target,
	    List<IDeviceStatus> existing) throws SiteWhereException {
	if (isUpdated(request.getCode(), target.getCode())) {
	    target.setCode(request.getCode());
	    checkDuplicateStatus(target, existing);
	}
	if (request.getName() != null) {
	    target.setName(request.getName());
	}
	if (request.getBackgroundColor() != null) {
	    target.setBackgroundColor(request.getBackgroundColor());
	}
	if (request.getForegroundColor() != null) {
	    target.setForegroundColor(request.getForegroundColor());
	}
	if (request.getBorderColor() != null) {
	    target.setBorderColor(request.getBorderColor());
	}
	if (request.getIcon() != null) {
	    target.setIcon(request.getIcon());
	}
	if (request.getMetadata() != null) {
	    target.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), target);
	}
    }

    /**
     * Checks whether a command is already in the given list (same name and
     * namespace).
     * 
     * @param command
     * @param existing
     * @throws SiteWhereException
     */
    protected static void checkDuplicateStatus(DeviceStatus status, List<IDeviceStatus> existing)
	    throws SiteWhereException {
	for (IDeviceStatus current : existing) {
	    if (current.getCode().equals(status.getCode())) {
		throw new SiteWhereSystemException(ErrorCode.DeviceStatusExists, ErrorLevel.ERROR);
	    }
	}
    }

    /**
     * Common logic for creating new device object and populating it from request.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static Device deviceCreateLogic(IDeviceCreateRequest request, ISite site, IDeviceSpecification spec)
	    throws SiteWhereException {
	Device device = new Device();
	device.setId(UUID.randomUUID());

	// Require hardware id and verify that it is valid.
	require(request.getHardwareId());
	Matcher matcher = HARDWARE_ID_REGEX.matcher(request.getHardwareId());
	if (!matcher.matches()) {
	    throw new SiteWhereSystemException(ErrorCode.MalformedHardwareId, ErrorLevel.ERROR);
	}
	device.setHardwareId(request.getHardwareId());
	device.setSiteId(site.getId());
	device.setDeviceSpecificationId(spec.getId());
	device.setComments(request.getComments());
	device.setStatus(request.getStatus());

	MetadataProvider.copy(request.getMetadata(), device);
	DeviceManagementPersistence.initializeEntityMetadata(device);
	return device;
    }

    /**
     * Common logic for updating a device object from request.
     * 
     * @param request
     * @param target
     * @throws SiteWhereException
     */
    public static void deviceUpdateLogic(IDeviceCreateRequest request, ISite site, IDeviceSpecification spec,
	    IDevice parent, Device target) throws SiteWhereException {
	// Can not update the hardware id on a device.
	if ((request.getHardwareId() != null) && (!request.getHardwareId().equals(target.getHardwareId()))) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceHardwareIdCanNotBeChanged, ErrorLevel.ERROR);
	}
	if (site != null) {
	    target.setSiteId(site.getId());
	}
	if (spec != null) {
	    target.setDeviceSpecificationId(spec.getId());
	}
	if (request.isRemoveParentHardwareId() == Boolean.TRUE) {
	    target.setParentDeviceId(null);
	}
	if (parent != null) {
	    target.setParentDeviceId(parent.getId());
	}
	if (request.getDeviceElementMappings() != null) {
	    List<DeviceElementMapping> mappings = new ArrayList<DeviceElementMapping>();
	    for (IDeviceElementMapping mapping : request.getDeviceElementMappings()) {
		mappings.add(DeviceElementMapping.copy(mapping));
	    }
	    target.setDeviceElementMappings(mappings);
	}
	if (request.getComments() != null) {
	    target.setComments(request.getComments());
	}
	if (request.getStatus() != null) {
	    target.setStatus(request.getStatus());
	}
	if (request.getMetadata() != null) {
	    target.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), target);
	}
	DeviceManagementPersistence.setUpdatedEntityMetadata(target);
    }

    /**
     * Encapsulates logic for creating a new {@link IDeviceElementMapping}.
     * 
     * @param management
     * @param device
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static IDevice deviceElementMappingCreateLogic(IDeviceManagement management, IDevice device,
	    IDeviceElementMapping request) throws SiteWhereException {
	if (device == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
	}
	IDevice mapped = management.getDeviceByHardwareId(request.getHardwareId());
	if (mapped == null) {
	    throw new SiteWhereException("Device referenced by mapping does not exist.");
	}

	// Check whether target device is already parented to another device.
	if (mapped.getParentDeviceId() != null) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceParentMappingExists, ErrorLevel.ERROR);
	}

	// Verify that requested path is valid on device specification.
	IDeviceSpecification specification = management.getDeviceSpecification(device.getDeviceSpecificationId());
	DeviceSpecificationUtils.getDeviceSlotByPath(specification, request.getDeviceElementSchemaPath());

	// Verify that there is not an existing mapping for the path.
	List<IDeviceElementMapping> existing = device.getDeviceElementMappings();
	List<DeviceElementMapping> newMappings = new ArrayList<DeviceElementMapping>();
	for (IDeviceElementMapping mapping : existing) {
	    if (mapping.getDeviceElementSchemaPath().equals(request.getDeviceElementSchemaPath())) {
		throw new SiteWhereSystemException(ErrorCode.DeviceElementMappingExists, ErrorLevel.ERROR);
	    }
	    newMappings.add(DeviceElementMapping.copy(mapping));
	}
	DeviceElementMapping newMapping = DeviceElementMapping.copy(request);
	newMappings.add(newMapping);

	// Add parent backreference for nested device.
	DeviceCreateRequest nested = new DeviceCreateRequest();
	nested.setParentHardwareId(device.getHardwareId());
	management.updateDevice(mapped.getId(), nested);

	// Update device with new mapping.
	DeviceCreateRequest update = new DeviceCreateRequest();
	update.setDeviceElementMappings(newMappings);
	IDevice updated = management.updateDevice(device.getId(), update);
	return updated;
    }

    /**
     * Encapsulates logic for deleting an {@link IDeviceElementMapping}.
     * 
     * @param management
     * @param device
     * @param path
     * @return
     * @throws SiteWhereException
     */
    public static IDevice deviceElementMappingDeleteLogic(IDeviceManagement management, IDevice device, String path)
	    throws SiteWhereException {
	if (device == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
	}

	// Verify that mapping exists and build list without deleted mapping.
	List<IDeviceElementMapping> existing = device.getDeviceElementMappings();
	List<DeviceElementMapping> newMappings = new ArrayList<DeviceElementMapping>();
	IDeviceElementMapping match = null;
	for (IDeviceElementMapping mapping : existing) {
	    if (mapping.getDeviceElementSchemaPath().equals(path)) {
		match = mapping;
	    } else {
		newMappings.add(DeviceElementMapping.copy(mapping));
	    }
	}
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceElementMappingDoesNotExist, ErrorLevel.ERROR);
	}

	// Remove parent reference from nested device.
	IDevice mapped = management.getDeviceByHardwareId(match.getHardwareId());
	if (mapped != null) {
	    DeviceCreateRequest nested = new DeviceCreateRequest();
	    nested.setRemoveParentHardwareId(true);
	    management.updateDevice(mapped.getId(), nested);
	}

	// Update device with new mappings.
	DeviceCreateRequest update = new DeviceCreateRequest();
	update.setDeviceElementMappings(newMappings);
	IDevice updated = management.updateDevice(device.getId(), update);
	return updated;
    }

    /**
     * Common logic for creating new site object and populating it from request.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static Site siteCreateLogic(ISiteCreateRequest request) throws SiteWhereException {
	Site site = new Site();
	site.setId(UUID.randomUUID());

	if (request.getToken() != null) {
	    site.setToken(request.getToken());
	} else {
	    site.setToken(UUID.randomUUID().toString());
	}

	site.setName(request.getName());
	site.setDescription(request.getDescription());
	site.setImageUrl(request.getImageUrl());
	site.setMap(SiteMapData.copy(request.getMap()));

	DeviceManagementPersistence.initializeEntityMetadata(site);
	MetadataProvider.copy(request.getMetadata(), site);
	return site;
    }

    /**
     * Common logic for copying data from site update request to existing site.
     * 
     * @param request
     * @param target
     * @throws SiteWhereException
     */
    public static void siteUpdateLogic(ISiteCreateRequest request, Site target) throws SiteWhereException {
	if (request.getName() != null) {
	    target.setName(request.getName());
	}
	if (request.getDescription() != null) {
	    target.setDescription(request.getDescription());
	}
	if (request.getImageUrl() != null) {
	    target.setImageUrl(request.getImageUrl());
	}
	if (request.getMap() != null) {
	    target.setMap(SiteMapData.copy(request.getMap()));
	}
	if (request.getMetadata() != null) {
	    target.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), target);
	}
	DeviceManagementPersistence.setUpdatedEntityMetadata(target);
    }

    /**
     * Common logic for creating a device assignment from a request.
     * 
     * @param source
     * @param device
     * @param uuid
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAssignment deviceAssignmentCreateLogic(IDeviceAssignmentCreateRequest source, IDevice device)
	    throws SiteWhereException {
	DeviceAssignment newAssignment = new DeviceAssignment();
	newAssignment.setId(UUID.randomUUID());
	newAssignment.setToken(source.getToken());
	newAssignment.setSiteId(device.getSiteId());
	newAssignment.setDeviceId(device.getId());

	requireNotNull(source.getAssignmentType());
	newAssignment.setAssignmentType(source.getAssignmentType());

	if (source.getAssignmentType() == DeviceAssignmentType.Associated) {
	    if (source.getAssetReference() == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidAssetReferenceId, ErrorLevel.ERROR);
	    }
	    newAssignment.setAssetReference(source.getAssetReference());
	}

	newAssignment.setActiveDate(new Date());
	newAssignment.setStatus(DeviceAssignmentStatus.Active);

	DeviceManagementPersistence.initializeEntityMetadata(newAssignment);
	MetadataProvider.copy(source.getMetadata(), newAssignment);

	return newAssignment;
    }

    /**
     * Logic applied before deleting a device assignment.
     * 
     * @param assignment
     * @throws SiteWhereException
     */
    public static void deviceAssignmentDeleteLogic(IDeviceAssignment assignment) throws SiteWhereException {
	if (assignment.getReleasedDate() == null) {
	    throw new SiteWhereSystemException(ErrorCode.CanNotDeleteActiveAssignment, ErrorLevel.ERROR);
	}
    }

    /**
     * Common logic for creating {@link DeviceStream} from
     * {@link IDeviceStreamCreateRequest}.
     * 
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStream deviceStreamCreateLogic(IDeviceAssignment assignment, IDeviceStreamCreateRequest request)
	    throws SiteWhereException {
	DeviceStream stream = new DeviceStream();
	stream.setId(UUID.randomUUID());
	stream.setAssignmentId(assignment.getId());

	// Verify the stream id is specified and contains only valid characters.
	require(request.getStreamId());
	if (!request.getStreamId().matches("^[a-zA-Z0-9_\\-]+$")) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidCharsInStreamId, ErrorLevel.ERROR);
	}
	stream.setStreamId(request.getStreamId());

	// Content type is required.
	require(request.getContentType());
	stream.setContentType(request.getContentType());

	MetadataProvider.copy(request.getMetadata(), stream);
	DeviceManagementPersistence.initializeEntityMetadata(stream);
	return stream;
    }

    /**
     * Verify that data supplied for command parameters is valid.
     * 
     * @param parameter
     * @param values
     * @throws SiteWhereException
     */
    protected static void checkData(ICommandParameter parameter, Map<String, String> values) throws SiteWhereException {

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
     * Common logic for creating a zone based on an incoming request.
     * 
     * @param source
     * @param siteToken
     * @param uuid
     * @return
     * @throws SiteWhereException
     */
    public static Zone zoneCreateLogic(IZoneCreateRequest source, ISite site, String uuid) throws SiteWhereException {
	Zone zone = new Zone();
	zone.setId(UUID.randomUUID());
	zone.setToken(uuid);
	zone.setSiteId(site.getId());
	zone.setName(source.getName());
	zone.setBorderColor(source.getBorderColor());
	zone.setFillColor(source.getFillColor());
	zone.setOpacity(source.getOpacity());

	DeviceManagementPersistence.initializeEntityMetadata(zone);
	MetadataProvider.copy(source.getMetadata(), zone);

	for (ILocation coordinate : source.getCoordinates()) {
	    zone.getCoordinates().add(coordinate);
	}
	return zone;
    }

    /**
     * Common code for copying information from an update request to an existing
     * zone.
     * 
     * @param request
     * @param target
     * @throws SiteWhereException
     */
    public static void zoneUpdateLogic(IZoneCreateRequest request, Zone target) throws SiteWhereException {
	target.setName(request.getName());
	target.setBorderColor(request.getBorderColor());
	target.setFillColor(request.getFillColor());
	target.setOpacity(request.getOpacity());

	target.getCoordinates().clear();
	for (ILocation coordinate : request.getCoordinates()) {
	    target.getCoordinates().add(coordinate);
	}

	if (request.getMetadata() != null) {
	    target.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), target);
	}
	DeviceManagementPersistence.setUpdatedEntityMetadata(target);
    }

    /**
     * Common logic for creating a device group based on an incoming request.
     * 
     * @param source
     * @param uuid
     * @return
     * @throws SiteWhereException
     */
    public static DeviceGroup deviceGroupCreateLogic(IDeviceGroupCreateRequest source, String uuid)
	    throws SiteWhereException {
	DeviceGroup group = new DeviceGroup();
	group.setId(UUID.randomUUID());
	group.setToken(uuid);
	group.setName(source.getName());
	group.setDescription(source.getDescription());
	if (source.getRoles() != null) {
	    group.getRoles().addAll(source.getRoles());
	}

	DeviceManagementPersistence.initializeEntityMetadata(group);
	MetadataProvider.copy(source.getMetadata(), group);
	return group;
    }

    /**
     * Common logic for updating an existing device group.
     * 
     * @param request
     * @param target
     * @throws SiteWhereException
     */
    public static void deviceGroupUpdateLogic(IDeviceGroupCreateRequest request, DeviceGroup target)
	    throws SiteWhereException {
	target.setName(request.getName());
	target.setDescription(request.getDescription());

	if (request.getRoles() != null) {
	    target.getRoles().clear();
	    target.getRoles().addAll(request.getRoles());
	}

	if (request.getMetadata() != null) {
	    target.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), target);
	}
	DeviceManagementPersistence.setUpdatedEntityMetadata(target);
    }

    /**
     * Common logic for creating a new device group element.
     * 
     * @param source
     * @param group
     * @param index
     * @param elementId
     * @return
     * @throws SiteWhereException
     */
    public static DeviceGroupElement deviceGroupElementCreateLogic(IDeviceGroupElementCreateRequest source,
	    IDeviceGroup group, long index, UUID elementId) throws SiteWhereException {
	DeviceGroupElement element = new DeviceGroupElement();
	element.setGroupId(group.getId());
	element.setIndex(index);
	element.setType(source.getType());
	element.setElementId(elementId);
	element.setRoles(source.getRoles());
	return element;
    }
}