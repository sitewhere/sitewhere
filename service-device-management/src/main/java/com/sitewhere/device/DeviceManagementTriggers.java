/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice;
import com.sitewhere.device.spi.microservice.IDeviceManagementTenantEngine;
import com.sitewhere.rest.model.device.event.request.DeviceStateChangeCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;

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
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.DeviceManagementDecorator#createDeviceAssignment(com
     * .sitewhere .spi.device.request.IDeviceAssignmentCreateRequest)
     */
    @Override
    public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request) throws SiteWhereException {
	IDeviceAssignment created = super.createDeviceAssignment(request);

	updateTenantAuthentication();
	DeviceStateChangeCreateRequest state = new DeviceStateChangeCreateRequest();
	state.setCategory(IDeviceStateChangeCreateRequest.CATEGORY_ASSIGNMENT);
	state.setType("create");
	getDeviceEventManagement().addDeviceStateChange(created.getId(), state);
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

	updateTenantAuthentication();
	DeviceStateChangeCreateRequest state = new DeviceStateChangeCreateRequest();
	state.setCategory(IDeviceStateChangeCreateRequest.CATEGORY_ASSIGNMENT);
	state.setType("update");
	getDeviceEventManagement().addDeviceStateChange(updated.getId(), state);
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

	updateTenantAuthentication();
	DeviceStateChangeCreateRequest state = new DeviceStateChangeCreateRequest();
	state.setCategory(IDeviceStateChangeCreateRequest.CATEGORY_ASSIGNMENT);
	state.setType("end");
	getDeviceEventManagement().addDeviceStateChange(updated.getId(), state);
	return updated;
    }

    /**
     * Update authentication for current thread to include current tenant and a
     * valid system user.
     * 
     * @throws SiteWhereException
     */
    protected void updateTenantAuthentication() throws SiteWhereException {
	Authentication system = getMicroservice().getSystemUser()
		.getAuthenticationForTenant(getDeviceManagementTenantEngine().getTenant());
	SecurityContextHolder.getContext().setAuthentication(system);
    }

    protected IDeviceEventManagement getDeviceEventManagement() throws SiteWhereException {
	return ((IDeviceManagementMicroservice) getDeviceManagementTenantEngine().getMicroservice())
		.getEventManagementApiDemux().getApiChannel();
    }

    protected IDeviceManagementTenantEngine getDeviceManagementTenantEngine() {
	return deviceManagementTenantEngine;
    }

    protected void setDeviceManagementTenantEngine(IDeviceManagementTenantEngine deviceManagementTenantEngine) {
	this.deviceManagementTenantEngine = deviceManagementTenantEngine;
    }
}