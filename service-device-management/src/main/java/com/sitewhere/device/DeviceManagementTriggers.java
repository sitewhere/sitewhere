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
	getDeviceInteractionEventsProducer().send(device.getId(), marshaled);
    }

    protected IDeviceInteractionEventsProducer getDeviceInteractionEventsProducer() {
	return getDeviceManagementTenantEngine().getDeviceInteractionEventsProducer();
    }

    protected IDeviceManagementTenantEngine getDeviceManagementTenantEngine() {
	return deviceManagementTenantEngine;
    }
}