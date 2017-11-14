/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.processing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.kafka.model.KafkaModel.GInboundEventPayload;
import com.sitewhere.grpc.model.DeviceEventModel.GAnyDeviceEventCreateRequest;
import com.sitewhere.grpc.model.converter.EventModelConverter;
import com.sitewhere.grpc.model.marshaling.KafkaModelMarshaler;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingMicroservice;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;

/**
 * Processing logic which verifies that an incoming event belongs to a
 * registered device. If the event does not belong to a registered device, it is
 * added to a Kafka topic that can be processed by a registration manager to
 * register the device automatically if so configured. The logic also verifies
 * that an active assignment exists for the device. Finally, the event is
 * persisted via the event management microservice.
 * 
 * @author Derek
 */
public class InboundPayloadProcessingLogic {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Handle to inbound processing tenant engine */
    private IInboundProcessingTenantEngine tenantEngine;

    public InboundPayloadProcessingLogic(IInboundProcessingTenantEngine tenantEngine) {
	this.tenantEngine = tenantEngine;
    }

    /**
     * Process an inbound event payload.
     * 
     * @param payload
     * @throws SiteWhereException
     */
    public void process(GInboundEventPayload payload) throws SiteWhereException {
	// Verify that device is registered.
	IDevice device = getDeviceManagement().getDeviceByHardwareId(payload.getHardwareId());
	if (device == null) {
	    handleUnregisteredDevice(payload);
	    return;
	}

	// Verify that device is assigned.
	if (device.getAssignmentToken() == null) {
	    handleUnassignedDevice(payload);
	    return;
	}

	IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(device.getAssignmentToken());
	if (assignment == null) {
	    getLogger().info("Assignment information for " + payload.getHardwareId() + " is invalid.");
	    handleUnassignedDevice(payload);
	    return;
	}

	// Store device event via the management APIs.
	storeDeviceEvent(assignment, payload);
    }

    /**
     * Store a device event via the device event management APIs.
     * 
     * @param assignment
     * @param payload
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceEvent storeDeviceEvent(IDeviceAssignment assignment, GInboundEventPayload payload)
	    throws SiteWhereException {
	GAnyDeviceEventCreateRequest grpc = payload.getEvent();
	IDeviceEventCreateRequest request = EventModelConverter.asApiDeviceEventCreateRequest(grpc);
	switch (request.getEventType()) {
	case Measurements:
	    return getDeviceEventManagement().addDeviceMeasurements(assignment,
		    (IDeviceMeasurementsCreateRequest) request);
	case Alert:
	    return getDeviceEventManagement().addDeviceAlert(assignment, (IDeviceAlertCreateRequest) request);
	case CommandInvocation:
	    return getDeviceEventManagement().addDeviceCommandInvocation(assignment,
		    (IDeviceCommandInvocationCreateRequest) request);
	case CommandResponse:
	    return getDeviceEventManagement().addDeviceCommandResponse(assignment,
		    (IDeviceCommandResponseCreateRequest) request);
	case Location:
	    return getDeviceEventManagement().addDeviceLocation(assignment, (IDeviceLocationCreateRequest) request);
	case StateChange:
	    return getDeviceEventManagement().addDeviceStateChange(assignment,
		    (IDeviceStateChangeCreateRequest) request);
	case StreamData: {
	    IDeviceStreamDataCreateRequest sdreq = (IDeviceStreamDataCreateRequest) request;
	    IDeviceStream stream = getDeviceManagement().getDeviceStream(assignment.getToken(), sdreq.getStreamId());
	    if (stream != null) {
		return getDeviceEventManagement().addDeviceStreamData(assignment, stream, sdreq);
	    } else {
		throw new SiteWhereException("Stream data references invalid stream: " + sdreq.getStreamId());
	    }
	}
	default:
	    throw new SiteWhereException("Unknown event type sent for storage: " + request.getEventType().name());
	}
    }

    /**
     * Handle case where event is processed for an unregistered device. Forwards
     * information to an out-of-band topic to be processed later.
     * 
     * @param payload
     * @throws SiteWhereException
     */
    protected void handleUnregisteredDevice(GInboundEventPayload payload) throws SiteWhereException {
	getLogger().info(
		"Device " + payload.getHardwareId() + " is not registered. Forwarding to unregistered devices topic.");
	byte[] marshaled = KafkaModelMarshaler.buildInboundEventPayloadMessage(payload);
	getTenantEngine().getUnregisteredDeviceEventsProducer().send(payload.getHardwareId(), marshaled);
	return;
    }

    /**
     * Handle case where event is sent for an unassigned device. Forwards
     * information to an out-of-band topic to be processed later.
     * 
     * @param payload
     * @throws SiteWhereException
     */
    protected void handleUnassignedDevice(GInboundEventPayload payload) throws SiteWhereException {
	getLogger().info("Device " + payload.getHardwareId()
		+ " is not currently assigned. Forwarding to unassigned devices topic.");
	byte[] marshaled = KafkaModelMarshaler.buildInboundEventPayloadMessage(payload);
	getTenantEngine().getUnregisteredDeviceEventsProducer().send(payload.getHardwareId(), marshaled);
	return;
    }

    /**
     * Get device management implementation.
     * 
     * @return
     */
    protected IDeviceManagement getDeviceManagement() {
	return ((IInboundProcessingMicroservice) getTenantEngine().getMicroservice()).getDeviceManagementApiChannel();
    }

    /**
     * Get device event management implementation.
     * 
     * @return
     */
    protected IDeviceEventManagement getDeviceEventManagement() {
	return ((IInboundProcessingMicroservice) getTenantEngine().getMicroservice())
		.getDeviceEventManagementApiChannel();
    }

    public Logger getLogger() {
	return LOGGER;
    }

    public IInboundProcessingTenantEngine getTenantEngine() {
	return tenantEngine;
    }

    public void setTenantEngine(IInboundProcessingTenantEngine tenantEngine) {
	this.tenantEngine = tenantEngine;
    }
}