/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device;

import java.util.UUID;

import com.sitewhere.device.spi.kafka.IDeviceInteractionEventsProducer;
import com.sitewhere.device.spi.microservice.IDeviceManagementTenantEngine;
import com.sitewhere.grpc.event.EventModelMarshaler;
import com.sitewhere.microservice.api.device.DeviceManagementDecorator;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.rest.model.device.event.kafka.DecodedEventPayload;
import com.sitewhere.rest.model.device.event.request.DeviceStateChangeCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;

/**
 * Adds triggers for processing related to device management API calls.
 */
public class DeviceManagementTriggers extends DeviceManagementDecorator {

    /** System event source id */
    private static final String SYSTEM_SOURCE_PREFIX = "system:";

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
	getLogger().info(String.format("About to look for device '%s'", created.getDeviceId()));
	IDevice device = super.getDevice(created.getDeviceId());

	DeviceStateChangeCreateRequest state = new DeviceStateChangeCreateRequest();
	state.setAttribute(IDeviceStateChangeCreateRequest.ATTRIBUTE_ASSIGNMENT);
	state.setType("create");
	produceEvent(device, state);
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
	IDevice device = super.getDevice(updated.getDeviceId());

	DeviceStateChangeCreateRequest state = new DeviceStateChangeCreateRequest();
	state.setAttribute(IDeviceStateChangeCreateRequest.ATTRIBUTE_ASSIGNMENT);
	state.setType("update");
	produceEvent(device, state);
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
	IDevice device = super.getDevice(updated.getDeviceId());

	DeviceStateChangeCreateRequest state = new DeviceStateChangeCreateRequest();
	state.setAttribute(IDeviceStateChangeCreateRequest.ATTRIBUTE_ASSIGNMENT);
	state.setType("end");
	produceEvent(device, state);
	return updated;
    }

    /**
     * Produce event to Kafka topic for creation by event management.
     * 
     * @param device
     * @param request
     * @throws SiteWhereException
     */
    protected void produceEvent(IDevice device, IDeviceEventCreateRequest request) throws SiteWhereException {
	DecodedEventPayload payload = new DecodedEventPayload();
	payload.setDeviceToken(device.getToken());
	payload.setSourceId(SYSTEM_SOURCE_PREFIX + getMicroservice().getIdentifier().getPath());
	payload.setEventCreateRequest(request);

	byte[] marshaled = EventModelMarshaler.buildDecodedEventPayloadMessage(payload);
	getDeviceInteractionEventsProducer().send(device.getToken(), marshaled);
    }

    protected IDeviceInteractionEventsProducer getDeviceInteractionEventsProducer() {
	return getDeviceManagementTenantEngine().getDeviceInteractionEventsProducer();
    }

    protected IDeviceManagementTenantEngine getDeviceManagementTenantEngine() {
	return deviceManagementTenantEngine;
    }
}