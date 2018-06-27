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
import com.sitewhere.rest.model.area.Area;
import com.sitewhere.rest.model.area.AreaType;
import com.sitewhere.rest.model.area.Zone;
import com.sitewhere.rest.model.common.Location;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.customer.Customer;
import com.sitewhere.rest.model.customer.CustomerType;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceElementMapping;
import com.sitewhere.rest.model.device.DeviceStatus;
import com.sitewhere.rest.model.device.DeviceType;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.element.DeviceElementSchema;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.rest.model.search.device.DeviceAssignmentSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.area.request.IAreaCreateRequest;
import com.sitewhere.spi.area.request.IAreaTypeCreateRequest;
import com.sitewhere.spi.area.request.IZoneCreateRequest;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.customer.ICustomerType;
import com.sitewhere.spi.customer.request.ICustomerCreateRequest;
import com.sitewhere.spi.customer.request.ICustomerTypeCreateRequest;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.IDeviceType;
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
import com.sitewhere.spi.device.request.IDeviceStatusCreateRequest;
import com.sitewhere.spi.device.request.IDeviceTypeCreateRequest;
import com.sitewhere.spi.device.util.DeviceTypeUtils;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Common methods needed by device management implementations.
 * 
 * @author Derek
 */
public class DeviceManagementPersistence extends Persistence {

    /** Regular expression used to validate hardware ids */
    private static final Pattern HARDWARE_ID_REGEX = Pattern.compile("^[\\w-]+$");

    /**
     * Common logic for creating new customer type object and populating it from
     * request.
     * 
     * @param request
     * @param containedCustomerTypeIds
     * @return
     * @throws SiteWhereException
     */
    public static CustomerType customerTypeCreateLogic(ICustomerTypeCreateRequest request,
	    List<UUID> containedCustomerTypeIds) throws SiteWhereException {
	CustomerType type = new CustomerType();
	type.setId(UUID.randomUUID());

	// Use token if provided, otherwise generate one.
	if (request.getToken() != null) {
	    type.setToken(request.getToken());
	} else {
	    type.setToken(UUID.randomUUID().toString());
	}

	type.setName(request.getName());
	type.setDescription(request.getDescription());
	type.setIcon(request.getIcon());
	type.setContainedCustomerTypeIds(containedCustomerTypeIds);

	DeviceManagementPersistence.initializeEntityMetadata(type);
	MetadataProvider.copy(request.getMetadata(), type);
	return type;
    }

    /**
     * Common logic for copying data from customer type update request to existing
     * customer type.
     * 
     * @param request
     * @param containedCustomerTypeIds
     * @param target
     * @throws SiteWhereException
     */
    public static void customerTypeUpdateLogic(ICustomerTypeCreateRequest request, List<UUID> containedCustomerTypeIds,
	    CustomerType target) throws SiteWhereException {
	if (request.getToken() != null) {
	    target.setToken(request.getToken());
	}
	if (request.getName() != null) {
	    target.setName(request.getName());
	}
	if (request.getDescription() != null) {
	    target.setDescription(request.getDescription());
	}
	if (request.getIcon() != null) {
	    target.setIcon(request.getIcon());
	}
	if (request.getContainedCustomerTypeTokens() != null) {
	    target.setContainedCustomerTypeIds(containedCustomerTypeIds);
	}
	if (request.getMetadata() != null) {
	    target.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), target);
	}
	DeviceManagementPersistence.setUpdatedEntityMetadata(target);
    }

    /**
     * Common logic for creating new customer object and populating it from request.
     * 
     * @param request
     * @param customerType
     * @param parentCustomer
     * @return
     * @throws SiteWhereException
     */
    public static Customer customerCreateLogic(ICustomerCreateRequest request, ICustomerType customerType,
	    ICustomer parentCustomer) throws SiteWhereException {
	Customer area = new Customer();
	area.setId(UUID.randomUUID());

	// Use token if provided, otherwise generate one.
	if (request.getToken() != null) {
	    area.setToken(request.getToken());
	} else {
	    area.setToken(UUID.randomUUID().toString());
	}

	area.setCustomerTypeId(customerType.getId());
	area.setParentCustomerId(parentCustomer != null ? parentCustomer.getId() : null);
	area.setName(request.getName());
	area.setDescription(request.getDescription());
	area.setImageUrl(request.getImageUrl());

	DeviceManagementPersistence.initializeEntityMetadata(area);
	MetadataProvider.copy(request.getMetadata(), area);
	return area;
    }

    /**
     * Common logic for copying data from customer update request to existing
     * customer.
     * 
     * @param request
     * @param target
     * @throws SiteWhereException
     */
    public static void customerUpdateLogic(ICustomerCreateRequest request, Customer target) throws SiteWhereException {
	if (request.getToken() != null) {
	    target.setToken(request.getToken());
	}
	if (request.getName() != null) {
	    target.setName(request.getName());
	}
	if (request.getDescription() != null) {
	    target.setDescription(request.getDescription());
	}
	if (request.getImageUrl() != null) {
	    target.setImageUrl(request.getImageUrl());
	}
	if (request.getMetadata() != null) {
	    target.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), target);
	}
	DeviceManagementPersistence.setUpdatedEntityMetadata(target);
    }

    /**
     * Common logic for creating new area type object and populating it from
     * request.
     * 
     * @param request
     * @param containedAreaTypeIds
     * @return
     * @throws SiteWhereException
     */
    public static AreaType areaTypeCreateLogic(IAreaTypeCreateRequest request, List<UUID> containedAreaTypeIds)
	    throws SiteWhereException {
	AreaType type = new AreaType();
	type.setId(UUID.randomUUID());

	// Use token if provided, otherwise generate one.
	if (request.getToken() != null) {
	    type.setToken(request.getToken());
	} else {
	    type.setToken(UUID.randomUUID().toString());
	}

	type.setName(request.getName());
	type.setDescription(request.getDescription());
	type.setIcon(request.getIcon());
	type.setContainedAreaTypeIds(containedAreaTypeIds);

	DeviceManagementPersistence.initializeEntityMetadata(type);
	MetadataProvider.copy(request.getMetadata(), type);
	return type;
    }

    /**
     * Common logic for copying data from area type update request to existing area
     * type.
     * 
     * @param request
     * @param containedAreaTypeIds
     * @param target
     * @throws SiteWhereException
     */
    public static void areaTypeUpdateLogic(IAreaTypeCreateRequest request, List<UUID> containedAreaTypeIds,
	    AreaType target) throws SiteWhereException {
	if (request.getToken() != null) {
	    target.setToken(request.getToken());
	}
	if (request.getName() != null) {
	    target.setName(request.getName());
	}
	if (request.getDescription() != null) {
	    target.setDescription(request.getDescription());
	}
	if (request.getIcon() != null) {
	    target.setIcon(request.getIcon());
	}
	if (request.getContainedAreaTypeTokens() != null) {
	    target.setContainedAreaTypeIds(containedAreaTypeIds);
	}
	if (request.getMetadata() != null) {
	    target.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), target);
	}
	DeviceManagementPersistence.setUpdatedEntityMetadata(target);
    }

    /**
     * Common logic for creating new area object and populating it from request.
     * 
     * @param request
     * @param areaType
     * @param parentArea
     * @return
     * @throws SiteWhereException
     */
    public static Area areaCreateLogic(IAreaCreateRequest request, IAreaType areaType, IArea parentArea)
	    throws SiteWhereException {
	Area area = new Area();
	area.setId(UUID.randomUUID());

	// Use token if provided, otherwise generate one.
	if (request.getToken() != null) {
	    area.setToken(request.getToken());
	} else {
	    area.setToken(UUID.randomUUID().toString());
	}

	area.setAreaTypeId(areaType.getId());
	area.setParentAreaId(parentArea != null ? parentArea.getId() : null);
	area.setName(request.getName());
	area.setDescription(request.getDescription());
	area.setImageUrl(request.getImageUrl());
	area.setBounds(Location.copy(request.getBounds()));

	DeviceManagementPersistence.initializeEntityMetadata(area);
	MetadataProvider.copy(request.getMetadata(), area);
	return area;
    }

    /**
     * Common logic for copying data from area update request to existing area.
     * 
     * @param request
     * @param target
     * @throws SiteWhereException
     */
    public static void areaUpdateLogic(IAreaCreateRequest request, Area target) throws SiteWhereException {
	if (request.getToken() != null) {
	    target.setToken(request.getToken());
	}
	if (request.getName() != null) {
	    target.setName(request.getName());
	}
	if (request.getDescription() != null) {
	    target.setDescription(request.getDescription());
	}
	if (request.getImageUrl() != null) {
	    target.setImageUrl(request.getImageUrl());
	}
	if (request.getBounds() != null) {
	    target.setBounds(Location.copy(request.getBounds()));
	}
	if (request.getMetadata() != null) {
	    target.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), target);
	}
	DeviceManagementPersistence.setUpdatedEntityMetadata(target);
    }

    /**
     * Common logic for creating new device type and populating it from request.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceType deviceTypeCreateLogic(IDeviceTypeCreateRequest request) throws SiteWhereException {
	DeviceType type = new DeviceType();
	type.setId(UUID.randomUUID());
	type.setDescription(request.getDescription() != null ? request.getDescription() : "");

	// Use token if provided, otherwise generate one.
	if (request.getToken() != null) {
	    type.setToken(request.getToken());
	} else {
	    type.setToken(UUID.randomUUID().toString());
	}

	// Name is required.
	require("Name", request.getName());
	type.setName(request.getName());

	// Image URL is required.
	require("Image URL", request.getImageUrl());
	type.setImageUrl(request.getImageUrl());

	// Container policy is required.
	requireNotNull("Container Policy", request.getContainerPolicy());
	type.setContainerPolicy(request.getContainerPolicy());

	// If composite container policy and no device element schema, create
	// empty schema.
	if (request.getContainerPolicy() == DeviceContainerPolicy.Composite) {
	    IDeviceElementSchema schema = request.getDeviceElementSchema();
	    if (schema == null) {
		schema = new DeviceElementSchema();
	    }
	    type.setDeviceElementSchema((DeviceElementSchema) schema);
	}

	MetadataProvider.copy(request.getMetadata(), type);
	DeviceManagementPersistence.initializeEntityMetadata(type);
	return type;
    }

    /**
     * Common logic for updating a device type from request.
     * 
     * @param request
     * @param target
     * @throws SiteWhereException
     */
    public static void deviceTypeUpdateLogic(IDeviceTypeCreateRequest request, DeviceType target)
	    throws SiteWhereException {
	if (request.getToken() != null) {
	    target.setToken(request.getToken());
	}
	if (request.getName() != null) {
	    target.setName(request.getName());
	}
	if (request.getDescription() != null) {
	    target.setDescription(request.getDescription());
	}
	if (request.getImageUrl() != null) {
	    target.setImageUrl(request.getImageUrl());
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
	if (request.getMetadata() != null) {
	    target.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), target);
	}
	DeviceManagementPersistence.setUpdatedEntityMetadata(target);
    }

    /**
     * Common logic executed before deleting a device type.
     * 
     * @param deviceType
     * @param deviceManagement
     * @throws SiteWhereException
     */
    public static void deviceTypeDeleteLogic(IDeviceType deviceType, IDeviceManagement deviceManagement)
	    throws SiteWhereException {
	DeviceSearchCriteria criteria = new DeviceSearchCriteria(1, 1, null, null);
	criteria.setDeviceTypeToken(deviceType.getToken());
	ISearchResults<IDevice> devices = deviceManagement.listDevices(false, criteria);
	if (devices.getNumResults() > 0) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceTypeInUseByDevices, ErrorLevel.ERROR);
	}
    }

    /**
     * Common logic for creating new device command and populating it from request.
     * 
     * @param deviceType
     * @param request
     * @param existing
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommand deviceCommandCreateLogic(IDeviceType deviceType, IDeviceCommandCreateRequest request,
	    List<IDeviceCommand> existing) throws SiteWhereException {
	DeviceCommand command = new DeviceCommand();
	command.setId(UUID.randomUUID());
	command.setDeviceTypeId(deviceType.getId());

	// Use token if provided, otherwise generate one.
	if (request.getToken() != null) {
	    command.setToken(request.getToken());
	} else {
	    command.setToken(UUID.randomUUID().toString());
	}

	// Name is required.
	require("Name", request.getName());
	command.setName(request.getName());

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
     * @param deviceType
     * @param request
     * @param target
     * @param existing
     * @throws SiteWhereException
     */
    public static void deviceCommandUpdateLogic(IDeviceType deviceType, IDeviceCommandCreateRequest request,
	    DeviceCommand target, List<IDeviceCommand> existing) throws SiteWhereException {
	if (request.getToken() != null) {
	    target.setToken(request.getToken());
	}
	if (request.getDeviceTypeToken() != null) {
	    target.setDeviceTypeId(deviceType.getId());
	}
	if (request.getName() != null) {
	    if (!request.getName().equals(target.getName())) {
		checkDuplicateCommand(target, existing);
		target.setName(request.getName());
	    }
	}
	if (request.getNamespace() != null) {
	    if (!request.getNamespace().equals(target.getNamespace())) {
		checkDuplicateCommand(target, existing);
		target.setNamespace(request.getNamespace());
	    }
	}

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
    public static DeviceStatus deviceStatusCreateLogic(IDeviceType deviceType, IDeviceStatusCreateRequest request,
	    List<IDeviceStatus> existing) throws SiteWhereException {
	DeviceStatus status = new DeviceStatus();
	status.setId(UUID.randomUUID());
	status.setDeviceTypeId(deviceType.getId());

	// Use token if provided, otherwise generate one.
	if (request.getToken() != null) {
	    status.setToken(request.getToken());
	} else {
	    status.setToken(UUID.randomUUID().toString());
	}

	// Code is required.
	require("Code", request.getCode());
	status.setCode(request.getCode());

	// Name is required.
	require("Name", request.getName());
	status.setName(request.getName());

	// Background color is required.
	require("Background color", request.getBackgroundColor());
	status.setBackgroundColor(request.getBackgroundColor());

	// Foreground color is required.
	require("Foreground color", request.getForegroundColor());
	status.setForegroundColor(request.getForegroundColor());

	// Border color is required.
	require("Border color", request.getBorderColor());
	status.setBorderColor(request.getBorderColor());

	// Icon is required.
	require("Icon", request.getIcon());
	status.setIcon(request.getIcon());

	checkDuplicateStatus(status, existing);

	MetadataProvider.copy(request.getMetadata(), status);
	DeviceManagementPersistence.initializeEntityMetadata(status);
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
    public static void deviceStatusUpdateLogic(IDeviceType deviceType, IDeviceStatusCreateRequest request,
	    DeviceStatus target, List<IDeviceStatus> existing) throws SiteWhereException {
	if (request.getToken() != null) {
	    target.setToken(request.getToken());
	}
	if (request.getDeviceTypeToken() != null) {
	    target.setDeviceTypeId(deviceType.getId());
	}
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
	DeviceManagementPersistence.setUpdatedEntityMetadata(target);
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
    public static Device deviceCreateLogic(IDeviceCreateRequest request, IDeviceType deviceType)
	    throws SiteWhereException {
	Device device = new Device();
	device.setId(UUID.randomUUID());

	// Use token if provided, otherwise generate one.
	if (request.getToken() != null) {
	    device.setToken(request.getToken());
	} else {
	    device.setToken(UUID.randomUUID().toString());
	}

	Matcher matcher = HARDWARE_ID_REGEX.matcher(request.getToken());
	if (!matcher.matches()) {
	    throw new SiteWhereSystemException(ErrorCode.MalformedHardwareId, ErrorLevel.ERROR);
	}
	device.setToken(request.getToken());
	device.setDeviceTypeId(deviceType.getId());
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
    public static void deviceUpdateLogic(IDeviceCreateRequest request, IDeviceType deviceType, IDevice parent,
	    Device target) throws SiteWhereException {
	if (deviceType != null) {
	    target.setDeviceTypeId(deviceType.getId());
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
     * Common logic executed before a device is deleted.
     * 
     * @param device
     * @param deviceManagement
     * @throws SiteWhereException
     */
    public static void deviceDeleteLogic(IDevice device, IDeviceManagement deviceManagement) throws SiteWhereException {
	if (device.getDeviceAssignmentId() != null) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceCanNotBeDeletedIfAssigned, ErrorLevel.ERROR);
	}

	DeviceAssignmentSearchCriteria criteria = new DeviceAssignmentSearchCriteria(1, 1);
	criteria.setDeviceId(device.getId());
	ISearchResults<IDeviceAssignment> assignments = deviceManagement.listDeviceAssignments(criteria);
	if (assignments.getNumResults() > 0) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceDeleteHasAssignments, ErrorLevel.ERROR);
	}
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
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceId, ErrorLevel.ERROR);
	}
	IDevice mapped = management.getDeviceByToken(request.getDeviceToken());
	if (mapped == null) {
	    throw new SiteWhereException("Device referenced by mapping does not exist.");
	}

	// Check whether target device is already parented to another device.
	if (mapped.getParentDeviceId() != null) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceParentMappingExists, ErrorLevel.ERROR);
	}

	// Verify that requested path is valid on device type.
	IDeviceType deviceType = management.getDeviceType(device.getDeviceTypeId());
	DeviceTypeUtils.getDeviceSlotByPath(deviceType, request.getDeviceElementSchemaPath());

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
	nested.setParentDeviceToken(device.getToken());
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
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceId, ErrorLevel.ERROR);
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
	IDevice mapped = management.getDeviceByToken(match.getDeviceToken());
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
     * Common logic for creating a device assignment from a request.
     * 
     * @param source
     * @param customer
     * @param area
     * @param asset
     * @param device
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAssignment deviceAssignmentCreateLogic(IDeviceAssignmentCreateRequest source,
	    ICustomer customer, IArea area, IAsset asset, IDevice device) throws SiteWhereException {
	DeviceAssignment assignment = new DeviceAssignment();
	assignment.setId(UUID.randomUUID());
	assignment.setToken(source.getToken());
	assignment.setCustomerId(customer != null ? customer.getId() : null);
	assignment.setAreaId(area != null ? area.getId() : null);
	assignment.setAssetId(asset != null ? asset.getId() : null);
	assignment.setDeviceId(device.getId());
	assignment.setDeviceTypeId(device.getDeviceTypeId());
	assignment.setActiveDate(new Date());
	assignment.setStatus(source.getStatus() != null ? source.getStatus() : DeviceAssignmentStatus.Active);

	DeviceManagementPersistence.initializeEntityMetadata(assignment);
	MetadataProvider.copy(source.getMetadata(), assignment);

	return assignment;
    }

    /**
     * Common logic for updating an existing device assignment.
     * 
     * @param device
     * @param customer
     * @param area
     * @param asset
     * @param request
     * @param target
     * @throws SiteWhereException
     */
    public static void deviceAssignmentUpdateLogic(IDevice device, ICustomer customer, IArea area, IAsset asset,
	    IDeviceAssignmentCreateRequest request, DeviceAssignment target) throws SiteWhereException {
	if (request.getToken() != null) {
	    target.setToken(request.getToken());
	}
	if (device != null) {
	    target.setDeviceId(device.getId());
	    target.setDeviceTypeId(device.getDeviceTypeId());
	}
	if (customer != null) {
	    target.setCustomerId(customer.getId());
	}
	if (area != null) {
	    target.setAreaId(area.getId());
	}
	if (asset != null) {
	    target.setAssetId(asset.getId());
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
	require("Stream Id", request.getStreamId());
	if (!request.getStreamId().matches("^[a-zA-Z0-9_\\-]+$")) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidCharsInStreamId, ErrorLevel.ERROR);
	}
	stream.setStreamId(request.getStreamId());

	// Content type is required.
	require("Content Type", request.getContentType());
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
     * @param request
     * @param area
     * @param uuid
     * @return
     * @throws SiteWhereException
     */
    public static Zone zoneCreateLogic(IZoneCreateRequest request, IArea area, String uuid) throws SiteWhereException {
	Zone zone = new Zone();
	zone.setId(UUID.randomUUID());

	// Use token if provided, otherwise generate one.
	if (request.getToken() != null) {
	    zone.setToken(request.getToken());
	} else {
	    zone.setToken(UUID.randomUUID().toString());
	}

	zone.setAreaId(area.getId());
	zone.setName(request.getName());
	zone.setBorderColor(request.getBorderColor());
	zone.setFillColor(request.getFillColor());
	zone.setOpacity(request.getOpacity());
	zone.setBounds(Location.copy(request.getBounds()));

	DeviceManagementPersistence.initializeEntityMetadata(zone);
	MetadataProvider.copy(request.getMetadata(), zone);

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
	if (request.getToken() != null) {
	    target.setToken(request.getToken());
	}
	if (request.getName() != null) {
	    target.setName(request.getName());
	}
	if (request.getBorderColor() != null) {
	    target.setBorderColor(request.getBorderColor());
	}
	if (request.getFillColor() != null) {
	    target.setFillColor(request.getFillColor());
	}
	if (request.getOpacity() != null) {
	    target.setOpacity(request.getOpacity());
	}
	if (request.getBounds() != null) {
	    target.setBounds(Location.copy(request.getBounds()));
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
    public static DeviceGroup deviceGroupCreateLogic(IDeviceGroupCreateRequest request) throws SiteWhereException {
	DeviceGroup group = new DeviceGroup();
	group.setId(UUID.randomUUID());
	group.setDescription(request.getDescription());

	// Use token if provided, otherwise generate one.
	if (request.getToken() != null) {
	    group.setToken(request.getToken());
	} else {
	    group.setToken(UUID.randomUUID().toString());
	}

	require("Name", request.getName());
	group.setName(request.getName());

	require("Image URL", request.getImageUrl());
	group.setImageUrl(request.getImageUrl());

	if (request.getRoles() != null) {
	    group.getRoles().addAll(request.getRoles());
	}

	DeviceManagementPersistence.initializeEntityMetadata(group);
	MetadataProvider.copy(request.getMetadata(), group);
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
	if (request.getName() != null) {
	    target.setName(request.getName());
	}
	if (request.getDescription() != null) {
	    target.setDescription(request.getDescription());
	}
	if (request.getImageUrl() != null) {
	    target.setImageUrl(request.getImageUrl());
	}
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
     * @param request
     * @param group
     * @param device
     * @param nested
     * @return
     * @throws SiteWhereException
     */
    public static DeviceGroupElement deviceGroupElementCreateLogic(IDeviceGroupElementCreateRequest request,
	    IDeviceGroup group, IDevice device, IDeviceGroup nested) throws SiteWhereException {
	DeviceGroupElement element = new DeviceGroupElement();
	element.setId(UUID.randomUUID());

	element.setGroupId(group.getId());
	element.setDeviceId(device != null ? device.getId() : null);
	element.setNestedGroupId(nested != null ? nested.getId() : null);
	element.setRoles(request.getRoles());
	return element;
    }
}