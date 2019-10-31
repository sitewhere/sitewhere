/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration;

import com.sitewhere.registration.spi.IRegistrationManager;
import com.sitewhere.registration.spi.microservice.IDeviceRegistrationMicroservice;
import com.sitewhere.rest.model.device.command.RegistrationAckCommand;
import com.sitewhere.rest.model.device.command.RegistrationFailureCommand;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.RegistrationFailureReason;
import com.sitewhere.spi.device.command.RegistrationSuccessReason;
import com.sitewhere.spi.device.event.kafka.IDecodedEventPayload;
import com.sitewhere.spi.device.event.kafka.IDeviceRegistrationPayload;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Base logic for {@link IRegistrationManager} implementations.
 * 
 * @author Derek
 */
public class DeviceRegistrationManager extends TenantEngineLifecycleComponent implements IRegistrationManager {

    /** Indicates if new devices can register with the system */
    private boolean allowNewDevices = false;

    /** Indicates if a default device type is used if not provided in request */
    private boolean useDefaultDeviceType = false;

    /** Id of device type that will be used if none provided */
    private String defaultDeviceTypeToken = null;

    /** Device type used if not provided in registration request */
    private IDeviceType defaultDeviceType;

    /** Indicates if a default customer used if not provided in request */
    private boolean useDefaultCustomer = false;

    /** Token of customer that will be used by default in assignment */
    private String defaultCustomerToken = null;

    /** Customer used for automatic assignment */
    private ICustomer defaultCustomer;

    /** Indicates if a default area used if not provided in request */
    private boolean useDefaultArea = false;

    /** Token of area that will be used by default in assignment */
    private String defaultAreaToken = null;

    /** Area used for automatic assignment */
    private IArea defaultArea;

    public DeviceRegistrationManager() {
	super(LifecycleComponentType.RegistrationManager);
    }

    /*
     * @see
     * com.sitewhere.registration.spi.IRegistrationManager#handleDeviceRegistration(
     * com.sitewhere.spi.microservice.kafka.payload.IDeviceRegistrationPayload)
     */
    @Override
    public void handleDeviceRegistration(IDeviceRegistrationPayload registration) throws SiteWhereException {
	// Authentication previous =
	// SecurityContextHolder.getContext().getAuthentication();
	// try {
	// Authentication system = getMicroservice().getSystemUser()
	// .getAuthenticationForTenant(getTenantEngine().getTenant());
	// SecurityContextHolder.getContext().setAuthentication(system);
	//
	// IDevice device = getOrCreateDevice(registration);
	//
	// // Find assignment metadata that should be associated.
	// ICustomer customer = getCustomerFor(registration);
	// IArea area = getAreaFor(registration);
	//
	// // Make sure device is assigned.
	// if (device.getActiveDeviceAssignmentIds().size() == 0) {
	// getLogger().debug("Handling unassigned device for registration.");
	// DeviceAssignmentCreateRequest assnCreate = new
	// DeviceAssignmentCreateRequest();
	// assnCreate.setDeviceToken(device.getToken());
	// if (customer != null) {
	// assnCreate.setCustomerToken(customer.getToken());
	// }
	// if (area != null) {
	// assnCreate.setAreaToken(area.getToken());
	// }
	// getDeviceManagement().createDeviceAssignment(assnCreate);
	// }
	// boolean isNewRegistration = (device != null);
	// sendRegistrationAck(registration.getDeviceToken(), isNewRegistration);
	// } finally {
	// SecurityContextHolder.getContext().setAuthentication(previous);
	// }
    }

    /**
     * Get existing device or create a new one based on registration request.
     * 
     * @param registration
     * @return
     * @throws SiteWhereException
     */
    protected IDevice getOrCreateDevice(IDeviceRegistrationPayload registration) throws SiteWhereException {
	IDevice device = getDeviceManagement().getDeviceByToken(registration.getDeviceToken());
	IDeviceCreateRequest request = registration.getDeviceRegistrationRequest();
	if (device == null) {
	    if (!isAllowNewDevices()) {
		throw new SiteWhereException("Ignoring device registration request. New devices are not allowed.");
	    }
	    // Create device if it does not already exist.
	    getLogger().info("Creating new device as part of registration.");
	    DeviceCreateRequest deviceCreate = new DeviceCreateRequest();
	    deviceCreate.setToken(request.getToken() != null ? request.getToken() : registration.getDeviceToken());
	    deviceCreate.setDeviceTypeToken(request.getDeviceTypeToken());
	    deviceCreate.setStatus(request.getStatus());
	    deviceCreate.setDeviceElementMappings(request.getDeviceElementMappings());
	    deviceCreate.setParentDeviceToken(request.getParentDeviceToken());
	    deviceCreate.setComments(request.getComments() != null ? request.getComments()
		    : "Device created by on-demand registration.");
	    deviceCreate.setMetadata(request.getMetadata());
	    return getDeviceManagement().createDevice(deviceCreate);
	} else {
	    getLogger().info("Found existing device registration. Updating device information.");
	    return getDeviceManagement().updateDevice(device.getId(), request);
	}
    }

    /*
     * @see com.sitewhere.registration.spi.IRegistrationManager#
     * handleUnregisteredDeviceEvent(com.sitewhere.spi.device.event.kafka.
     * IDecodedEventPayload)
     */
    @Override
    public void handleUnregisteredDeviceEvent(IDecodedEventPayload payload) throws SiteWhereException {
	getLogger().info("Would be handling unregistered device event for " + payload.getDeviceToken());
    }

    /**
     * Get device type that should be used for the given request.
     * 
     * @param registration
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceType getDeviceTypeFor(IDeviceRegistrationPayload registration) throws SiteWhereException {
	String deviceTypeToken = registration.getDeviceRegistrationRequest().getDeviceTypeToken();
	if (deviceTypeToken != null) {
	    IDeviceType override = getDeviceManagement().getDeviceTypeByToken(deviceTypeToken);
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
     * @param registration
     * @return
     * @throws SiteWhereException
     */
    protected ICustomer getCustomerFor(IDeviceRegistrationPayload registration) throws SiteWhereException {
	String customerToken = registration.getDeviceRegistrationRequest().getCustomerToken();
	if (customerToken != null) {
	    ICustomer override = getDeviceManagement().getCustomerByToken(customerToken);
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
     * @param registration
     * @return
     * @throws SiteWhereException
     */
    protected IArea getAreaFor(IDeviceRegistrationPayload registration) throws SiteWhereException {
	String areaToken = registration.getDeviceRegistrationRequest().getAreaToken();
	if (areaToken != null) {
	    IArea override = getDeviceManagement().getAreaByToken(areaToken);
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Authentication previous =
	// SecurityContextHolder.getContext().getAuthentication();
	// try {
	// Authentication system = getMicroservice().getSystemUser()
	// .getAuthenticationForTenant(getTenantEngine().getTenant());
	// SecurityContextHolder.getContext().setAuthentication(system);
	//
	// if (getDefaultDeviceTypeToken() != null) {
	// IDeviceType deviceType =
	// getDeviceManagement().getDeviceTypeByToken(getDefaultDeviceTypeToken());
	// if (deviceType == null) {
	// throw new SiteWhereException("Registration manager auto assignment device
	// type is invalid.");
	// }
	// DeviceRegistrationManager.this.defaultDeviceType = deviceType;
	// }
	// if (getDefaultCustomerToken() != null) {
	// ICustomer customer =
	// getDeviceManagement().getCustomerByToken(getDefaultCustomerToken());
	// if (customer == null) {
	// throw new SiteWhereException("Registration manager auto assignment customer
	// is invalid.");
	// }
	// DeviceRegistrationManager.this.defaultCustomer = customer;
	// }
	// if (getDefaultAreaToken() != null) {
	// IArea area = getDeviceManagement().getAreaByToken(getDefaultAreaToken());
	// if (area == null) {
	// throw new SiteWhereException("Registration manager auto assignment area is
	// invalid.");
	// }
	// DeviceRegistrationManager.this.defaultArea = area;
	// }
	// } finally {
	// SecurityContextHolder.getContext().setAuthentication(previous);
	// }
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
     * com.sitewhere.registration.spi.IRegistrationManager#getDefaultDeviceTypeToken
     * ()
     */
    @Override
    public String getDefaultDeviceTypeToken() {
	return defaultDeviceTypeToken;
    }

    public void setDefaultDeviceTypeToken(String defaultDeviceTypeToken) {
	this.defaultDeviceTypeToken = defaultDeviceTypeToken;
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
     * com.sitewhere.registration.spi.IRegistrationManager#getDefaultCustomerToken()
     */
    @Override
    public String getDefaultCustomerToken() {
	return defaultCustomerToken;
    }

    public void setDefaultCustomerToken(String defaultCustomerToken) {
	this.defaultCustomerToken = defaultCustomerToken;
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
     * @see
     * com.sitewhere.registration.spi.IRegistrationManager#getDefaultAreaToken()
     */
    @Override
    public String getDefaultAreaToken() {
	return defaultAreaToken;
    }

    public void setDefaultAreaToken(String defaultAreaToken) {
	this.defaultAreaToken = defaultAreaToken;
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
	return ((IDeviceRegistrationMicroservice) getTenantEngine().getMicroservice()).getDeviceManagementApiChannel();
    }
}