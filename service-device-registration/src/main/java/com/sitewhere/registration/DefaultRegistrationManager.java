/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration;

import java.util.UUID;

import com.sitewhere.registration.spi.IRegistrationManager;
import com.sitewhere.registration.spi.microservice.IDeviceRegistrationMicroservice;
import com.sitewhere.rest.model.device.DeviceElementMapping;
import com.sitewhere.rest.model.device.command.DeviceMappingAckCommand;
import com.sitewhere.rest.model.device.command.RegistrationAckCommand;
import com.sitewhere.rest.model.device.command.RegistrationFailureCommand;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.DeviceMappingResult;
import com.sitewhere.spi.device.command.RegistrationFailureReason;
import com.sitewhere.spi.device.command.RegistrationSuccessReason;
import com.sitewhere.spi.device.event.request.IDeviceMappingCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.microservice.kafka.payload.IInboundEventPayload;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Base logic for {@link IRegistrationManager} implementations.
 * 
 * @author Derek
 */
public class DefaultRegistrationManager extends TenantEngineLifecycleComponent implements IRegistrationManager {

    /** Indicates if new devices can register with the system */
    private boolean allowNewDevices = false;

    /** Indicates if a default device type is used if not provided in request */
    private boolean useDefaultDeviceType = false;

    /** Id of device type that will be used if none provided */
    private UUID defaultDeviceTypeId = null;

    /** Device type used if not provided in registration request */
    private IDeviceType defaultDeviceType;

    /** Indicates if a default customer used if not provided in request */
    private boolean useDefaultCustomer = false;

    /** Id of customer that will be used by default in assignment */
    private UUID defaultCustomerId = null;

    /** Customer used for automatic assignment */
    private ICustomer defaultCustomer;

    /** Indicates if a default area used if not provided in request */
    private boolean useDefaultArea = false;

    /** Id of area that will be used by default in assignment */
    private UUID defaultAreaId = null;

    /** Area used for automatic assignment */
    private IArea defaultArea;

    public DefaultRegistrationManager() {
	super(LifecycleComponentType.RegistrationManager);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IRegistrationManager#
     * handleDeviceRegistration
     * (com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest)
     */
    @Override
    public void handleDeviceRegistration(IDeviceRegistrationRequest request) throws SiteWhereException {
	getLogger().debug("Handling device registration request.");
	IDevice device = getOrCreateDevice(request);

	// Find assignment metadata that should be associated.
	ICustomer customer = getCustomerFor(request);
	IArea area = getAreaFor(request);

	// Make sure device is assigned.
	if (device.getDeviceAssignmentId() == null) {
	    getLogger().debug("Handling unassigned device for registration.");
	    DeviceAssignmentCreateRequest assnCreate = new DeviceAssignmentCreateRequest();
	    assnCreate.setDeviceToken(device.getToken());
	    if (customer != null) {
		assnCreate.setCustomerToken(customer.getToken());
	    }
	    if (area != null) {
		assnCreate.setAreaToken(area.getToken());
	    }
	    getDeviceManagement().createDeviceAssignment(assnCreate);
	}
	boolean isNewRegistration = (device != null);
	sendRegistrationAck(request.getDeviceToken(), isNewRegistration);
    }

    /**
     * Get existing device or create a new one based on registration request.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    protected IDevice getOrCreateDevice(IDeviceRegistrationRequest request) throws SiteWhereException {
	IDevice device = getDeviceManagement().getDeviceByToken(request.getDeviceToken());
	IDeviceType deviceType = getDeviceTypeFor(request);

	// Create device if it does not already exist.
	if (device == null) {
	    if (!isAllowNewDevices()) {
		throw new SiteWhereException("Ignoring device registration request since new devices are not allowed.");
	    }
	    getLogger().debug("Creating new device as part of registration.");
	    DeviceCreateRequest deviceCreate = new DeviceCreateRequest();
	    deviceCreate.setToken(request.getDeviceToken());
	    deviceCreate.setDeviceTypeToken(request.getDeviceTypeToken());
	    deviceCreate.setComments("Device created by on-demand registration.");
	    deviceCreate.setMetadata(request.getMetadata());
	    return getDeviceManagement().createDevice(deviceCreate);
	} else if (!device.getDeviceTypeId().equals(deviceType.getId())) {
	    sendInvalidDeviceType(request.getDeviceToken());
	    throw new SiteWhereException("Found existing device registration, but device type does not match.");
	} else {
	    getLogger().info("Found existing device registration. Updating metadata.");
	    DeviceCreateRequest deviceUpdate = new DeviceCreateRequest();
	    deviceUpdate.setMetadata(request.getMetadata());
	    return getDeviceManagement().updateDevice(device.getId(), deviceUpdate);
	}
    }

    /*
     * @see com.sitewhere.registration.spi.IRegistrationManager#
     * handleUnregisteredDeviceEvent(com.sitewhere.spi.microservice.kafka.payload.
     * IInboundEventPayload)
     */
    @Override
    public void handleUnregisteredDeviceEvent(IInboundEventPayload payload) throws SiteWhereException {
	getLogger().info("Would be handling unregistered device event for " + payload.getDeviceToken());
    }

    /**
     * Get device type that should be used for the given request.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceType getDeviceTypeFor(IDeviceRegistrationRequest request) throws SiteWhereException {
	if (request.getDeviceTypeToken() != null) {
	    IDeviceType override = getDeviceManagement().getDeviceTypeByToken(request.getDeviceTypeToken());
	    if (override == null) {
		throw new SiteWhereException("Registration request specified invalid device type token.");
	    }
	    return override;
	} else if (isUseDefaultDeviceType()) {
	    return getDefaultDeviceType();
	}
	throw new SiteWhereException("Device type not passed and no default provided.");
    }

    /**
     * Get customer that should be used for the given request.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    protected ICustomer getCustomerFor(IDeviceRegistrationRequest request) throws SiteWhereException {
	if (request.getCustomerToken() != null) {
	    ICustomer override = getDeviceManagement().getCustomerByToken(request.getCustomerToken());
	    if (override == null) {
		throw new SiteWhereException("Registration request specified invalid customer token.");
	    }
	    return override;
	} else if (isUseDefaultCustomer()) {
	    return getDefaultCustomer();
	}
	return null;
    }

    /**
     * Get area that should be used for the given request.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    protected IArea getAreaFor(IDeviceRegistrationRequest request) throws SiteWhereException {
	if (request.getAreaToken() != null) {
	    IArea override = getDeviceManagement().getAreaByToken(request.getAreaToken());
	    if (override == null) {
		throw new SiteWhereException("Registration request specified invalid area token.");
	    }
	    return override;
	} else if (isUseDefaultArea()) {
	    return getDefaultArea();
	}
	return null;
    }

    /**
     * Send a registration ack message.
     * 
     * @param hardwareId
     * @param newRegistration
     * @throws SiteWhereException
     */
    protected void sendRegistrationAck(String hardwareId, boolean newRegistration) throws SiteWhereException {
	RegistrationAckCommand command = new RegistrationAckCommand();
	command.setReason((newRegistration) ? RegistrationSuccessReason.NewRegistration
		: RegistrationSuccessReason.AlreadyRegistered);
	// getDeviceCommunication().deliverSystemCommand(hardwareId, command);
    }

    /**
     * Send a message indicating that the registration manager does not allow
     * registration of new devices.
     * 
     * @param hardwareId
     * @throws SiteWhereException
     */
    protected void sendNoNewDevicesAllowed(String hardwareId) throws SiteWhereException {
	RegistrationFailureCommand command = new RegistrationFailureCommand();
	command.setReason(RegistrationFailureReason.NewDevicesNotAllowed);
	command.setErrorMessage("Registration manager does not allow new devices to be created.");
	// getDeviceCommunication().deliverSystemCommand(hardwareId, command);
    }

    /**
     * Send a message indicating invalid device type id or one that does not match
     * existing device.
     * 
     * @param hardwareId
     * @throws SiteWhereException
     */
    protected void sendInvalidDeviceType(String hardwareId) throws SiteWhereException {
	RegistrationFailureCommand command = new RegistrationFailureCommand();
	command.setReason(RegistrationFailureReason.InvalidDeviceTypeToken);
	command.setErrorMessage("Device type token passed in registration was invalid.");
	// getDeviceCommunication().deliverSystemCommand(hardwareId, command);
    }

    /**
     * Send information indicating a site token must be passed (if not
     * auto-assigned).
     * 
     * @param hardwareId
     * @throws SiteWhereException
     */
    protected void sendSiteTokenRequired(String hardwareId) throws SiteWhereException {
	RegistrationFailureCommand command = new RegistrationFailureCommand();
	command.setReason(RegistrationFailureReason.SiteTokenRequired);
	command.setErrorMessage("Automatic site assignment disabled. Site token required.");
	// getDeviceCommunication().deliverSystemCommand(hardwareId, command);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IRegistrationManager#
     * handleDeviceMapping(java.lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceMappingCreateRequest)
     */
    @Override
    public void handleDeviceMapping(String deviceToken, IDeviceMappingCreateRequest request) throws SiteWhereException {
	DeviceElementMapping mapping = new DeviceElementMapping();
	mapping.setDeviceToken(deviceToken);
	mapping.setDeviceElementSchemaPath(request.getMappingPath());
	DeviceMappingAckCommand command = new DeviceMappingAckCommand();
	try {
	    IDevice existing = getDeviceManagement().getDeviceByToken(deviceToken);
	    getDeviceManagement().createDeviceElementMapping(existing.getId(), mapping);
	    command.setResult(DeviceMappingResult.MappingCreated);
	} catch (SiteWhereException e) {
	    command.setResult(DeviceMappingResult.MappingFailedDueToExisting);
	}
	// getDeviceCommunication().deliverSystemCommand(hardwareId, command);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getDefaultDeviceTypeId() != null) {
	    IDeviceType deviceType = getDeviceManagement().getDeviceType(getDefaultDeviceTypeId());
	    if (deviceType == null) {
		throw new SiteWhereException("Registration manager auto assignment device type is invalid.");
	    }
	    this.defaultDeviceType = deviceType;
	}
	if (getDefaultCustomerId() != null) {
	    ICustomer customer = getDeviceManagement().getCustomer(getDefaultCustomerId());
	    if (customer == null) {
		throw new SiteWhereException("Registration manager auto assignment customer is invalid.");
	    }
	    this.defaultCustomer = customer;
	}
	if (getDefaultAreaId() != null) {
	    IArea area = getDeviceManagement().getArea(getDefaultAreaId());
	    if (area == null) {
		throw new SiteWhereException("Registration manager auto assignment area is invalid.");
	    }
	    this.defaultArea = area;
	}
    }

    /*
     * @see com.sitewhere.registration.spi.IRegistrationManager#isAllowNewDevices()
     */
    @Override
    public boolean isAllowNewDevices() {
	return allowNewDevices;
    }

    public void setAllowNewDevices(boolean allowNewDevices) {
	this.allowNewDevices = allowNewDevices;
    }

    /*
     * @see
     * com.sitewhere.registration.spi.IRegistrationManager#isUseDefaultDeviceType()
     */
    @Override
    public boolean isUseDefaultDeviceType() {
	return useDefaultDeviceType;
    }

    public void setUseDefaultDeviceType(boolean useDefaultDeviceType) {
	this.useDefaultDeviceType = useDefaultDeviceType;
    }

    /*
     * @see
     * com.sitewhere.registration.spi.IRegistrationManager#getDefaultDeviceTypeId()
     */
    @Override
    public UUID getDefaultDeviceTypeId() {
	return defaultDeviceTypeId;
    }

    public void setDefaultDeviceTypeId(UUID defaultDeviceTypeId) {
	this.defaultDeviceTypeId = defaultDeviceTypeId;
    }

    /*
     * @see
     * com.sitewhere.registration.spi.IRegistrationManager#isUseDefaultCustomer()
     */
    @Override
    public boolean isUseDefaultCustomer() {
	return useDefaultCustomer;
    }

    public void setUseDefaultCustomer(boolean useDefaultCustomer) {
	this.useDefaultCustomer = useDefaultCustomer;
    }

    /*
     * @see
     * com.sitewhere.registration.spi.IRegistrationManager#getDefaultCustomerId()
     */
    @Override
    public UUID getDefaultCustomerId() {
	return defaultCustomerId;
    }

    public void setDefaultCustomerId(UUID defaultCustomerId) {
	this.defaultCustomerId = defaultCustomerId;
    }

    /*
     * @see com.sitewhere.registration.spi.IRegistrationManager#isUseDefaultArea()
     */
    @Override
    public boolean isUseDefaultArea() {
	return useDefaultArea;
    }

    public void setUseDefaultArea(boolean useDefaultArea) {
	this.useDefaultArea = useDefaultArea;
    }

    /*
     * @see com.sitewhere.registration.spi.IRegistrationManager#getDefaultAreaId()
     */
    @Override
    public UUID getDefaultAreaId() {
	return defaultAreaId;
    }

    public void setDefaultAreaId(UUID defaultAreaId) {
	this.defaultAreaId = defaultAreaId;
    }

    protected IDeviceType getDefaultDeviceType() {
	return defaultDeviceType;
    }

    protected void setDefaultDeviceType(IDeviceType defaultDeviceType) {
	this.defaultDeviceType = defaultDeviceType;
    }

    protected ICustomer getDefaultCustomer() {
	return defaultCustomer;
    }

    protected void setDefaultCustomer(ICustomer defaultCustomer) {
	this.defaultCustomer = defaultCustomer;
    }

    protected IArea getDefaultArea() {
	return defaultArea;
    }

    protected void setDefaultArea(IArea defaultArea) {
	this.defaultArea = defaultArea;
    }

    private IDeviceManagement getDeviceManagement() {
	return ((IDeviceRegistrationMicroservice) getTenantEngine().getMicroservice()).getDeviceManagementApiDemux()
		.getApiChannel();
    }
}