/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.processing;

import com.sitewhere.grpc.client.event.BlockingDeviceEventManagement;
import com.sitewhere.grpc.kafka.model.KafkaModel.GInboundEventPayload;
import com.sitewhere.grpc.model.DeviceEventModel.GAnyDeviceEventCreateRequest;
import com.sitewhere.grpc.model.converter.EventModelConverter;
import com.sitewhere.inbound.spi.microservice.IInboundEventStorageStrategy;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingMicroservice;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
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
 * Event storage strategy that sends each event via a unary GRPC call.
 * 
 * @author Derek
 */
public class UnaryEventStorageStrategy implements IInboundEventStorageStrategy {

    /** Handle to inbound processing tenant engine */
    private IInboundProcessingTenantEngine tenantEngine;

    public UnaryEventStorageStrategy(IInboundProcessingTenantEngine tenantEngine) {
	this.tenantEngine = tenantEngine;
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
	case Measurements:
	    getDeviceEventManagement().addDeviceMeasurements(assignment.getId(),
		    (IDeviceMeasurementsCreateRequest) request);
	    break;
	case Alert:
	    getDeviceEventManagement().addDeviceAlert(assignment.getId(), (IDeviceAlertCreateRequest) request);
	    break;
	case CommandInvocation:
	    getDeviceEventManagement().addDeviceCommandInvocation(assignment.getId(),
		    (IDeviceCommandInvocationCreateRequest) request);
	    break;
	case CommandResponse:
	    getDeviceEventManagement().addDeviceCommandResponse(assignment.getId(),
		    (IDeviceCommandResponseCreateRequest) request);
	    break;
	case Location:
	    getDeviceEventManagement().addDeviceLocation(assignment.getId(), (IDeviceLocationCreateRequest) request);
	    break;
	case StateChange:
	    getDeviceEventManagement().addDeviceStateChange(assignment.getId(),
		    (IDeviceStateChangeCreateRequest) request);
	    break;
	case StreamData: {
	    IDeviceStreamDataCreateRequest sdreq = (IDeviceStreamDataCreateRequest) request;
	    IDeviceStream stream = getDeviceManagement().getDeviceStream(assignment.getId(), sdreq.getStreamId());
	    if (stream != null) {
		getDeviceEventManagement().addDeviceStreamData(assignment.getId(), stream, sdreq);
	    } else {
		throw new SiteWhereException("Stream data references invalid stream: " + sdreq.getStreamId());
	    }
	    break;
	}
	default:
	    throw new SiteWhereException("Unknown event type sent for storage: " + request.getEventType().name());
	}
    }

    /**
     * Get device management implementation.
     * 
     * @return
     */
    protected IDeviceManagement getDeviceManagement() {
	return ((IInboundProcessingMicroservice) getTenantEngine().getMicroservice()).getDeviceManagementApiDemux()
		.getApiChannel();
    }

    /**
     * Get device event management implementation.
     * 
     * @return
     */
    protected IDeviceEventManagement getDeviceEventManagement() {
	return new BlockingDeviceEventManagement(((IInboundProcessingMicroservice) getTenantEngine().getMicroservice())
		.getDeviceEventManagementApiDemux().getApiChannel());
    }

    public IInboundProcessingTenantEngine getTenantEngine() {
	return tenantEngine;
    }

    public void setTenantEngine(IInboundProcessingTenantEngine tenantEngine) {
	this.tenantEngine = tenantEngine;
    }
}