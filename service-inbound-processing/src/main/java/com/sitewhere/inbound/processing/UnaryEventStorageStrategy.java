/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.processing;

import com.sitewhere.grpc.client.event.EventModelConverter;
import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.grpc.model.DeviceEventModel.GAnyDeviceEventCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GInboundEventPayload;
import com.sitewhere.inbound.spi.microservice.IInboundEventStorageStrategy;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingMicroservice;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;

/**
 * Event storage strategy that sends each event via a unary GRPC call.
 * 
 * @author Derek
 */
public class UnaryEventStorageStrategy implements IInboundEventStorageStrategy {

    /** Handle to inbound processing tenant engine */
    private IInboundProcessingTenantEngine tenantEngine;

    /** Get processing logic */
    private InboundPayloadProcessingLogic inboundPayloadProcessingLogic;

    public UnaryEventStorageStrategy(IInboundProcessingTenantEngine tenantEngine,
	    InboundPayloadProcessingLogic inboundPayloadProcessingLogic) {
	this.tenantEngine = tenantEngine;
	this.inboundPayloadProcessingLogic = inboundPayloadProcessingLogic;
    }

    /*
     * @see com.sitewhere.inbound.spi.microservice.IInboundEventStorageStrategy#
     * storeDeviceEvent(com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.grpc.kafka.model.KafkaModel.GInboundEventPayload)
     */
    @Override
    public void storeDeviceEvent(IDeviceAssignment assignment, GInboundEventPayload payload) throws SiteWhereException {
	GAnyDeviceEventCreateRequest grpc = payload.getEvent();
	IDeviceEventCreateRequest request = EventModelConverter.asApiDeviceEventCreateRequest(grpc);
	switch (request.getEventType()) {
	case Measurement:
	    getDeviceEventManagement().addDeviceMeasurements(assignment.getId(),
		    new AlertHandlerStreamObserver<>(getInboundPayloadProcessingLogic()),
		    (IDeviceMeasurementCreateRequest) request);
	    break;
	case Alert:
	    getDeviceEventManagement().addDeviceAlerts(assignment.getId(),
		    new AlertHandlerStreamObserver<>(getInboundPayloadProcessingLogic()),
		    (IDeviceAlertCreateRequest) request);
	    break;
	case CommandInvocation:
	    getDeviceEventManagement().addDeviceCommandInvocations(assignment.getId(),
		    new AlertHandlerStreamObserver<>(getInboundPayloadProcessingLogic()),
		    (IDeviceCommandInvocationCreateRequest) request);
	    break;
	case CommandResponse:
	    getDeviceEventManagement().addDeviceCommandResponses(assignment.getId(),
		    new AlertHandlerStreamObserver<>(getInboundPayloadProcessingLogic()),
		    (IDeviceCommandResponseCreateRequest) request);
	    break;
	case Location:
	    getDeviceEventManagement().addDeviceLocations(assignment.getId(),
		    new AlertHandlerStreamObserver<>(getInboundPayloadProcessingLogic()),
		    (IDeviceLocationCreateRequest) request);
	    break;
	case StateChange:
	    getDeviceEventManagement().addDeviceStateChanges(assignment.getId(),
		    new AlertHandlerStreamObserver<>(getInboundPayloadProcessingLogic()),
		    (IDeviceStateChangeCreateRequest) request);
	    break;
	default:
	    throw new SiteWhereException("Unknown event type sent for storage: " + request.getEventType().name());
	}
    }

    /**
     * Send alert payload via event management api.
     * 
     * @param assignment
     * @param request
     * @throws SiteWhereException
     */
    protected void sendAlert(IDeviceAssignment assignment, IDeviceAlertCreateRequest request)
	    throws SiteWhereException {
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
    protected IDeviceEventManagementApiChannel<?> getDeviceEventManagement() {
	return ((IInboundProcessingMicroservice) getTenantEngine().getMicroservice())
		.getDeviceEventManagementApiChannel();
    }

    protected IInboundProcessingTenantEngine getTenantEngine() {
	return tenantEngine;
    }

    protected void setTenantEngine(IInboundProcessingTenantEngine tenantEngine) {
	this.tenantEngine = tenantEngine;
    }

    protected InboundPayloadProcessingLogic getInboundPayloadProcessingLogic() {
	return inboundPayloadProcessingLogic;
    }

    protected void setInboundPayloadProcessingLogic(InboundPayloadProcessingLogic inboundPayloadProcessingLogic) {
	this.inboundPayloadProcessingLogic = inboundPayloadProcessingLogic;
    }
}