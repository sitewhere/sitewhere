/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(DefaultRegistrationManager.class);

    /** Indicates if new devices can register with the system */
    private boolean allowNewDevices = true;

    /** Id of device type that will be auto-assigned */
    private UUID autoAssignDeviceTypeId = null;

    /** Device type used for automatic assignment */
    private IDeviceType autoAssignDeviceType;

    /** Id of area that will be auto-assigned */
    private UUID autoAssignAreaId = null;

    /** Area used for automatic assignment */
    private IArea autoAssignArea;

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
	LOGGER.debug("Handling device registration request.");
	IDevice device = getDeviceManagement().getDeviceByToken(request.getDeviceToken());
	IDeviceType deviceType = getDeviceTypeFor(request);

	// Create device if it does not already exist.
	if (device == null) {
	    if (!isAllowNewDevices()) {
		LOGGER.warn("Ignoring device registration request since new devices are not allowed.");
		return;
	    }
	    LOGGER.debug("Creating new device as part of registration.");
	    DeviceCreateRequest deviceCreate = new DeviceCreateRequest();
	    deviceCreate.setToken(request.getDeviceToken());
	    deviceCreate.setDeviceTypeToken(request.getDeviceTypeToken());
	    deviceCreate.setComments("Device created by on-demand registration.");
	    deviceCreate.setMetadata(request.getMetadata());
	    device = getDeviceManagement().createDevice(deviceCreate);
	} else if (!device.getDeviceTypeId().equals(deviceType.getId())) {
	    LOGGER.info("Found existing device registration, but device type does not match.");
	    sendInvalidDeviceType(request.getDeviceToken());
	    return;
	} else {
	    LOGGER.info("Found existing device registration. Updating metadata.");
	    DeviceCreateRequest deviceUpdate = new DeviceCreateRequest();
	    deviceUpdate.setMetadata(request.getMetadata());
	    device = getDeviceManagement().updateDevice(device.getId(), deviceUpdate);
	}

	// Make sure device is assigned.
	if (device.getDeviceAssignmentId() == null) {
	    LOGGER.debug("Handling unassigned device for registration.");
	    DeviceAssignmentCreateRequest assnCreate = new DeviceAssignmentCreateRequest();
	    assnCreate.setDeviceToken(device.getToken());
	    getDeviceManagement().createDeviceAssignment(assnCreate);
	}
	boolean isNewRegistration = (device != null);
	sendRegistrationAck(request.getDeviceToken(), isNewRegistration);
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
     * Get device specificatoin that should be used for the given request.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceType getDeviceTypeFor(IDeviceRegistrationRequest request) throws SiteWhereException {
	IDeviceType deviceType = getAutoAssignDeviceType();
	if (request.getDeviceTypeToken() != null) {
	    IDeviceType override = getDeviceManagement().getDeviceTypeByToken(request.getDeviceTypeToken());
	    if (override == null) {
		throw new SiteWhereException("Registration request specified invalid device type token.");
	    }
	    deviceType = override;
	}
	if (deviceType == null) {
	    throw new SiteWhereException("Device type not passed and no default provided.");
	}
	return deviceType;
    }

    /**
     * Get area that should be used for the given request.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    protected IArea getAreaFor(IDeviceRegistrationRequest request) throws SiteWhereException {
	IArea deviceArea = getAutoAssignArea();
	if (request.getAreaToken() != null) {
	    IArea override = getDeviceManagement().getAreaByToken(request.getAreaToken());
	    if (override == null) {
		throw new SiteWhereException("Registration request specified invalid area token.");
	    }
	    deviceArea = override;
	}
	if (deviceArea == null) {
	    throw new SiteWhereException("Area not passed and no default provided.");
	}
	return deviceArea;
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
	if (getAutoAssignDeviceTypeId() != null) {
	    IDeviceType deviceType = getDeviceManagement().getDeviceType(getAutoAssignDeviceTypeId());
	    if (deviceType == null) {
		throw new SiteWhereException("Registration manager auto assignment device type is invalid.");
	    }
	    this.autoAssignDeviceType = deviceType;
	}
	if (getAutoAssignAreaId() != null) {
	    IArea area = getDeviceManagement().getArea(getAutoAssignAreaId());
	    if (area == null) {
		throw new SiteWhereException("Registration manager auto assignment area is invalid.");
	    }
	    this.autoAssignArea = area;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }

    public boolean isAllowNewDevices() {
	return allowNewDevices;
    }

    public void setAllowNewDevices(boolean allowNewDevices) {
	this.allowNewDevices = allowNewDevices;
    }

    /*
     * @see
     * com.sitewhere.registration.spi.IRegistrationManager#getAutoAssignDeviceTypeId
     * ()
     */
    @Override
    public UUID getAutoAssignDeviceTypeId() {
	return autoAssignDeviceTypeId;
    }

    public void setAutoAssignDeviceTypeId(UUID autoAssignDeviceTypeId) {
	this.autoAssignDeviceTypeId = autoAssignDeviceTypeId;
    }

    public IDeviceType getAutoAssignDeviceType() {
	return autoAssignDeviceType;
    }

    public void setAutoAssignDeviceType(IDeviceType autoAssignDeviceType) {
	this.autoAssignDeviceType = autoAssignDeviceType;
    }

    /*
     * @see
     * com.sitewhere.registration.spi.IRegistrationManager#getAutoAssignAreaId()
     */
    @Override
    public UUID getAutoAssignAreaId() {
	return autoAssignAreaId;
    }

    public void setAutoAssignAreaId(UUID autoAssignAreaId) {
	this.autoAssignAreaId = autoAssignAreaId;
    }

    public IArea getAutoAssignArea() {
	return autoAssignArea;
    }

    public void setAutoAssignArea(IArea autoAssignArea) {
	this.autoAssignArea = autoAssignArea;
    }

    private IDeviceManagement getDeviceManagement() {
	return ((IDeviceRegistrationMicroservice) getTenantEngine().getMicroservice()).getDeviceManagementApiDemux()
		.getApiChannel();
    }
}