/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device;

import java.util.UUID;

import com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice;
import com.sitewhere.device.spi.microservice.IDeviceManagementTenantEngine;
import com.sitewhere.grpc.client.event.BlockingDeviceEventManagement;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.rest.model.device.event.request.DeviceStateChangeCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;

/**
 * Adds triggers for processing related to device management API calls.
 * 
 * @author Derek
 */
public class DeviceManagementTriggers extends DeviceManagementDecorator {

    /** Device management tenant engine */
    private IDeviceManagementTenantEngine deviceManagementTenantEngine;

    public DeviceManagementTriggers(IDeviceManagement delegate,
	    IDeviceManagementTenantEngine deviceManagementTenantEngine) {
	super(delegate);
	this.deviceManagementTenantEngine = deviceManagementTenantEngine;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#createDevice(com.sitewhere.spi
     * .device.request.IDeviceCreateRequest)
     */
    @Override
    public IDevice createDevice(IDeviceCreateRequest device) throws SiteWhereException {
	IDevice created = super.createDevice(device);

	// Create default assignment so device can start receiving events.
	DeviceAssignmentCreateRequest assnCreate = new DeviceAssignmentCreateRequest();
	assnCreate.setDeviceToken(created.getToken());
	assnCreate.setStatus(DeviceAssignmentStatus.Active);
	IDeviceAssignment assignment = getDeviceManagementTenantEngine().getDeviceManagement()
		.createDeviceAssignment(assnCreate);

	// Fire state change event as system user.
	(new SystemUserRunnable(getMicroservice(), getTenantEngine().getTenant()) {

	    @Override
	    public void runAsSystemUser() throws SiteWhereException {
		DeviceStateChangeCreateRequest state = new DeviceStateChangeCreateRequest();
		state.setAttribute(IDeviceStateChangeCreateRequest.ATTRIBUTE_REGISTRATION);
		state.setType("created");
		getDeviceEventManagement().addDeviceStateChanges(assignment.getId(), state);
	    }
	}).run();

	return created;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#updateDevice(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceCreateRequest)
     */
    @Override
    public IDevice updateDevice(UUID id, IDeviceCreateRequest request) throws SiteWhereException {
	IDevice updated = super.updateDevice(id, request);

	// No update events will be recorded if not assigned.
	if (updated.getDeviceAssignmentId() != null) {
	    // Fire state change event as system user.
	    (new SystemUserRunnable(getMicroservice(), getTenantEngine().getTenant()) {

		@Override
		public void runAsSystemUser() throws SiteWhereException {
		    DeviceStateChangeCreateRequest state = new DeviceStateChangeCreateRequest();
		    state.setAttribute(IDeviceStateChangeCreateRequest.ATTRIBUTE_REGISTRATION);
		    state.setType("updated");
		    getDeviceEventManagement().addDeviceStateChanges(updated.getDeviceAssignmentId(), state);
		}
	    }).run();
	}

	return updated;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#deleteDevice(java.util.UUID)
     */
    @Override
    public IDevice deleteDevice(UUID id) throws SiteWhereException {
	// Fire 'deleted' state change before deleting if assigned.
	IDevice existing = getDeviceManagementTenantEngine().getDeviceManagement().getDevice(id);
	if ((existing != null) && (existing.getDeviceAssignmentId() != null)) {
	    // Fire state change event as system user.
	    (new SystemUserRunnable(getMicroservice(), getTenantEngine().getTenant()) {

		@Override
		public void runAsSystemUser() throws SiteWhereException {
		    DeviceStateChangeCreateRequest state = new DeviceStateChangeCreateRequest();
		    state.setAttribute(IDeviceStateChangeCreateRequest.ATTRIBUTE_REGISTRATION);
		    state.setType("deleted");
		    getDeviceEventManagement().addDeviceStateChanges(existing.getDeviceAssignmentId(), state);
		}
	    }).run();

	    // End the device assignment to prevent hanging reference.
	    getDeviceManagementTenantEngine().getDeviceManagement()
		    .endDeviceAssignment(existing.getDeviceAssignmentId());
	}

	return super.deleteDevice(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#createDeviceAssignment(com
     * .sitewhere .spi.device.request.IDeviceAssignmentCreateRequest)
     */
    @Override
    public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request) throws SiteWhereException {
	if (request.getDeviceToken() == null) {
	    throw new SiteWhereException("Device token must be provided when creating an assignment.");
	}
	// Check for existing assignment before creating a new one.
	IDevice existing = getDeviceManagementTenantEngine().getDeviceManagement()
		.getDeviceByToken(request.getDeviceToken());
	if (existing.getDeviceAssignmentId() != null) {
	    // End previous device assignment before creating new one.
	    getDeviceManagementTenantEngine().getDeviceManagement()
		    .endDeviceAssignment(existing.getDeviceAssignmentId());
	}

	// Create new assignment.
	IDeviceAssignment created = super.createDeviceAssignment(request);

	// Fire state change event as system user.
	(new SystemUserRunnable(getMicroservice(), getTenantEngine().getTenant()) {

	    @Override
	    public void runAsSystemUser() throws SiteWhereException {
		DeviceStateChangeCreateRequest state = new DeviceStateChangeCreateRequest();
		state.setAttribute(IDeviceStateChangeCreateRequest.ATTRIBUTE_ASSIGNMENT);
		state.setType("create");
		getDeviceEventManagement().addDeviceStateChanges(created.getId(), state);
	    }
	}).run();

	return created;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#updateDeviceAssignment(java.
     * util.UUID, com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest)
     */
    @Override
    public IDeviceAssignment updateDeviceAssignment(UUID id, IDeviceAssignmentCreateRequest request)
	    throws SiteWhereException {
	IDeviceAssignment updated = super.updateDeviceAssignment(id, request);

	// Fire state change event as system user.
	(new SystemUserRunnable(getMicroservice(), getTenantEngine().getTenant()) {

	    @Override
	    public void runAsSystemUser() throws SiteWhereException {
		DeviceStateChangeCreateRequest state = new DeviceStateChangeCreateRequest();
		state.setAttribute(IDeviceStateChangeCreateRequest.ATTRIBUTE_ASSIGNMENT);
		state.setType("update");
		getDeviceEventManagement().addDeviceStateChanges(updated.getId(), state);
	    }
	}).run();

	return updated;
    }

    /*
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#endDeviceAssignment(java.util.
     * UUID)
     */
    @Override
    public IDeviceAssignment endDeviceAssignment(UUID id) throws SiteWhereException {
	IDeviceAssignment updated = super.endDeviceAssignment(id);

	// Fire state change event as system user.
	(new SystemUserRunnable(getMicroservice(), getTenantEngine().getTenant()) {

	    @Override
	    public void runAsSystemUser() throws SiteWhereException {
		DeviceStateChangeCreateRequest state = new DeviceStateChangeCreateRequest();
		state.setAttribute(IDeviceStateChangeCreateRequest.ATTRIBUTE_ASSIGNMENT);
		state.setType("end");
		getDeviceEventManagement().addDeviceStateChanges(updated.getId(), state);
	    }
	}).run();

	return updated;
    }

    protected IDeviceEventManagement getDeviceEventManagement() throws SiteWhereException {
	return new BlockingDeviceEventManagement(
		((IDeviceManagementMicroservice) getDeviceManagementTenantEngine().getMicroservice())
			.getEventManagementApiDemux().getApiChannel());
    }

    protected IDeviceManagementTenantEngine getDeviceManagementTenantEngine() {
	return deviceManagementTenantEngine;
    }

    protected void setDeviceManagementTenantEngine(IDeviceManagementTenantEngine deviceManagementTenantEngine) {
	this.deviceManagementTenantEngine = deviceManagementTenantEngine;
    }
}