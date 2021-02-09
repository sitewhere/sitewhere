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
package com.sitewhere.registration;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.inject.Inject;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.registration.configuration.DeviceRegistrationTenantConfiguration;
import com.sitewhere.registration.spi.IRegistrationManager;
import com.sitewhere.registration.spi.microservice.IDeviceRegistrationMicroservice;
import com.sitewhere.rest.model.device.command.RegistrationAckCommand;
import com.sitewhere.rest.model.device.command.RegistrationFailureCommand;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.RegistrationFailureReason;
import com.sitewhere.spi.device.command.RegistrationSuccessReason;
import com.sitewhere.spi.device.event.kafka.IDecodedEventPayload;
import com.sitewhere.spi.device.event.kafka.IDeviceRegistrationPayload;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Base logic for {@link IRegistrationManager} implementations.
 */
public class DeviceRegistrationManager extends TenantEngineLifecycleComponent implements IRegistrationManager {

    /** Configuration */
    private DeviceRegistrationTenantConfiguration configuration;

    /** Thread pool */
    private ExecutorService executor;

    @Inject
    public DeviceRegistrationManager(DeviceRegistrationTenantConfiguration configuration) {
	super(LifecycleComponentType.RegistrationManager);
	this.configuration = configuration;
	getLogger().info(String.format("Registration manager using configuration:\n%s\n\n",
		MarshalUtils.marshalJsonAsPrettyString(configuration)));
    }

    /*
     * @see com.sitewhere.microservice.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.executor = Executors.newSingleThreadExecutor();
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#start(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (isUseDefaultDeviceType() && getDefaultDeviceTypeToken() != null) {
	    getLogger()
		    .info(String.format("Registration manager will use default device type '%s' if none is specified.",
			    getDefaultDeviceTypeToken()));
	}
	if (isUseDefaultCustomer() && getDefaultCustomerToken() != null) {
	    getLogger().info(String.format("Registration manager will use default customer '%s' if none is specified.",
		    getDefaultCustomerToken()));
	}
	if (isUseDefaultArea() && getDefaultAreaToken() != null) {
	    getLogger().info(String.format("Registration manager will use default area '%s' if none is specified.",
		    getDefaultAreaToken()));
	}
	if (isUseDefaultAsset() && getDefaultAssetToken() != null) {
	    getLogger().info(String.format("Registration manager will use default asset '%s' if none is specified.",
		    getDefaultAssetToken()));
	}
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#stop(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getExecutor() != null) {
	    getExecutor().shutdownNow();
	}
    }

    /*
     * @see
     * com.sitewhere.registration.spi.IRegistrationManager#handleDeviceRegistration(
     * com.sitewhere.spi.microservice.kafka.payload.IDeviceRegistrationPayload)
     */
    @Override
    public void handleDeviceRegistration(IDeviceRegistrationPayload registration) throws SiteWhereException {
	getExecutor().execute(new RegistrationProcessor(this, registration));
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
     * Handles registration processing in a thread with system user access.
     */
    protected class RegistrationProcessor extends SystemUserRunnable {

	private IDeviceRegistrationPayload registration;

	public RegistrationProcessor(ITenantEngineLifecycleComponent component,
		IDeviceRegistrationPayload registration) {
	    super(component);
	    this.registration = registration;
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#runAsSystemUser()
	 */
	@Override
	public void runAsSystemUser() throws SiteWhereException {
	    IDevice device = getOrCreateDevice(registration);

	    // Find assignment metadata that should be associated.
	    ICustomer customer = getCustomerFor(registration);
	    IArea area = getAreaFor(registration);
	    IAsset asset = getAssetFor(registration);

	    // Make sure device is assigned.
	    List<? extends IDeviceAssignment> assignments = getDeviceManagement()
		    .getActiveDeviceAssignments(device.getId());
	    if (assignments.size() == 0) {
		getLogger().debug("Handling unassigned device for registration.");
		DeviceAssignmentCreateRequest assnCreate = new DeviceAssignmentCreateRequest();
		assnCreate.setDeviceToken(device.getToken());
		if (customer != null) {
		    assnCreate.setCustomerToken(customer.getToken());
		}
		if (area != null) {
		    assnCreate.setAreaToken(area.getToken());
		}
		if (asset != null) {
		    assnCreate.setAssetToken(asset.getToken());
		}
		getDeviceManagement().createDeviceAssignment(assnCreate);
	    }
	    boolean isNewRegistration = (device != null);
	    sendRegistrationAck(registration.getDeviceToken(), isNewRegistration);
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

	/**
	 * Get device type that should be used for the given request.
	 * 
	 * @param registration
	 * @return
	 * @throws SiteWhereException
	 */
	protected IDeviceType getDeviceTypeFor(IDeviceRegistrationPayload registration) throws SiteWhereException {
	    String deviceTypeToken = registration.getDeviceRegistrationRequest().getDeviceTypeToken();
	    if (deviceTypeToken == null) {
		if (isUseDefaultDeviceType()) {
		    deviceTypeToken = getDefaultDeviceTypeToken();
		} else {
		    throw new SiteWhereException("Device type not passed and no default provided.");
		}
	    }
	    return getDeviceManagement().getDeviceTypeByToken(deviceTypeToken);
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
	    if (customerToken == null && isUseDefaultCustomer()) {
		customerToken = getDefaultCustomerToken();
	    }
	    return customerToken != null ? getDeviceManagement().getCustomerByToken(customerToken) : null;
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
	    if (areaToken == null && isUseDefaultArea()) {
		areaToken = getDefaultAreaToken();
	    }
	    return areaToken != null ? getDeviceManagement().getAreaByToken(areaToken) : null;
	}

	/**
	 * Get asset that should be used for the given request.
	 * 
	 * @param registration
	 * @return
	 * @throws SiteWhereException
	 */
	protected IAsset getAssetFor(IDeviceRegistrationPayload registration) throws SiteWhereException {
	    String assetToken = registration.getDeviceRegistrationRequest().getAssetToken();
	    if (assetToken == null && isUseDefaultAsset()) {
		assetToken = getDefaultAssetToken();
	    }
	    return assetToken != null ? getAssetManagement().getAssetByToken(assetToken) : null;
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
    }

    /*
     * @see com.sitewhere.registration.spi.IRegistrationManager#isAllowNewDevices()
     */
    @Override
    public boolean isAllowNewDevices() {
	return getConfiguration().isAllowNewDevices();
    }

    /*
     * @see
     * com.sitewhere.registration.spi.IRegistrationManager#isUseDefaultDeviceType()
     */
    @Override
    public boolean isUseDefaultDeviceType() {
	return getConfiguration().getAssignmentDefaults().getDefaultDeviceTypeToken() != null;
    }

    /*
     * @see
     * com.sitewhere.registration.spi.IRegistrationManager#getDefaultDeviceTypeToken
     * ()
     */
    @Override
    public String getDefaultDeviceTypeToken() {
	return getConfiguration().getAssignmentDefaults().getDefaultDeviceTypeToken();
    }

    /*
     * @see
     * com.sitewhere.registration.spi.IRegistrationManager#isUseDefaultCustomer()
     */
    @Override
    public boolean isUseDefaultCustomer() {
	return getConfiguration().getAssignmentDefaults().getDefaultCustomerToken() != null;
    }

    /*
     * @see
     * com.sitewhere.registration.spi.IRegistrationManager#getDefaultCustomerToken()
     */
    @Override
    public String getDefaultCustomerToken() {
	return getConfiguration().getAssignmentDefaults().getDefaultCustomerToken();
    }

    /*
     * @see com.sitewhere.registration.spi.IRegistrationManager#isUseDefaultArea()
     */
    @Override
    public boolean isUseDefaultArea() {
	return getConfiguration().getAssignmentDefaults().getDefaultAreaToken() != null;
    }

    /*
     * @see com.sitewhere.registration.spi.IRegistrationManager#isUseDefaultAsset()
     */
    @Override
    public boolean isUseDefaultAsset() {
	return getConfiguration().getAssignmentDefaults().getDefaultAssetToken() != null;
    }

    /*
     * @see
     * com.sitewhere.registration.spi.IRegistrationManager#getDefaultAssetToken()
     */
    @Override
    public String getDefaultAssetToken() {
	return getConfiguration().getAssignmentDefaults().getDefaultAssetToken();
    }

    /*
     * @see
     * com.sitewhere.registration.spi.IRegistrationManager#getDefaultAreaToken()
     */
    @Override
    public String getDefaultAreaToken() {
	return getConfiguration().getAssignmentDefaults().getDefaultAreaToken();
    }

    public DeviceRegistrationTenantConfiguration getConfiguration() {
	return configuration;
    }

    protected ExecutorService getExecutor() {
	return executor;
    }

    private IDeviceManagement getDeviceManagement() {
	return ((IDeviceRegistrationMicroservice) getTenantEngine().getMicroservice()).getDeviceManagement();
    }

    private IAssetManagement getAssetManagement() {
	return ((IDeviceRegistrationMicroservice) getTenantEngine().getMicroservice()).getAssetManagement();
    }
}