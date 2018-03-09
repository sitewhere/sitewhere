/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.kafka;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.event.DeviceEventManagementDecorator;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.kafka.model.KafkaModel.GPersistedEventPayload;
import com.sitewhere.grpc.model.converter.KafkaModelConverter;
import com.sitewhere.grpc.model.marshaler.KafkaModelMarshaler;
import com.sitewhere.rest.model.microservice.kafka.payload.PersistedEventPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.IDeviceStreamData;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;

/**
 * Adds triggers to event persistence methods to push the new events into a
 * Kafka topic.
 * 
 * @author Derek
 */
public class KafkaEventPersistenceTriggers extends DeviceEventManagementDecorator {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(KafkaEventPersistenceTriggers.class);

    /** Parent tenant engine */
    private IEventManagementTenantEngine tenantEngine;

    public KafkaEventPersistenceTriggers(IEventManagementTenantEngine tenantEngine, IDeviceEventManagement delegate) {
	super(delegate);
	this.tenantEngine = tenantEngine;
    }

    /**
     * Forward the given event to the Kafka persisted events topic.
     * 
     * @param assignment
     * @param event
     * @return
     * @throws SiteWhereException
     */
    protected <T extends IDeviceEvent> T forwardEvent(IDeviceAssignment assignment, T event) throws SiteWhereException {
	long start = System.currentTimeMillis();
	try {
	    PersistedEventPayload api = new PersistedEventPayload();
	    api.setDeviceId(assignment.getDeviceId());
	    api.setEvent(event);
	    GPersistedEventPayload payload = KafkaModelConverter.asGrpcPersistedEventPayload(api);

	    getTenantEngine().getInboundPersistedEventsProducer().send(assignment.getToken(),
		    KafkaModelMarshaler.buildPersistedEventPayloadMessage(payload));
	    return event;
	} finally {
	    getLogger()
		    .trace("Forwarding persisted event to Kafka took " + (System.currentTimeMillis() - start) + " ms.");
	}
    }

    /*
     * @see
     * com.sitewhere.event.DeviceEventManagementDecorator#addDeviceMeasurements(
     * com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
     */
    @Override
    public IDeviceMeasurements addDeviceMeasurements(IDeviceAssignment assignment,
	    IDeviceMeasurementsCreateRequest measurements) throws SiteWhereException {
	return forwardEvent(assignment, super.addDeviceMeasurements(assignment, measurements));
    }

    /*
     * @see
     * com.sitewhere.event.DeviceEventManagementDecorator#addDeviceLocation(com.
     * sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
     */
    @Override
    public IDeviceLocation addDeviceLocation(IDeviceAssignment assignment, IDeviceLocationCreateRequest request)
	    throws SiteWhereException {
	return forwardEvent(assignment, super.addDeviceLocation(assignment, request));
    }

    /*
     * @see com.sitewhere.event.DeviceEventManagementDecorator#addDeviceAlert(com.
     * sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
     */
    @Override
    public IDeviceAlert addDeviceAlert(IDeviceAssignment assignment, IDeviceAlertCreateRequest request)
	    throws SiteWhereException {
	return forwardEvent(assignment, super.addDeviceAlert(assignment, request));
    }

    /*
     * @see com.sitewhere.event.DeviceEventManagementDecorator#addDeviceStreamData(
     * com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.streaming.IDeviceStream,
     * com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest)
     */
    @Override
    public IDeviceStreamData addDeviceStreamData(IDeviceAssignment assignment, IDeviceStream stream,
	    IDeviceStreamDataCreateRequest request) throws SiteWhereException {
	return forwardEvent(assignment, super.addDeviceStreamData(assignment, stream, request));
    }

    /*
     * @see com.sitewhere.event.DeviceEventManagementDecorator#
     * addDeviceCommandInvocation(com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.event.request.
     * IDeviceCommandInvocationCreateRequest)
     */
    @Override
    public IDeviceCommandInvocation addDeviceCommandInvocation(IDeviceAssignment assignment,
	    IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
	return forwardEvent(assignment, super.addDeviceCommandInvocation(assignment, request));
    }

    /*
     * @see com.sitewhere.event.DeviceEventManagementDecorator#
     * addDeviceCommandResponse(com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.event.request. IDeviceCommandResponseCreateRequest)
     */
    @Override
    public IDeviceCommandResponse addDeviceCommandResponse(IDeviceAssignment assignment,
	    IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
	return forwardEvent(assignment, super.addDeviceCommandResponse(assignment, request));
    }

    /*
     * @see com.sitewhere.event.DeviceEventManagementDecorator#addDeviceStateChange(
     * com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest)
     */
    @Override
    public IDeviceStateChange addDeviceStateChange(IDeviceAssignment assignment,
	    IDeviceStateChangeCreateRequest request) throws SiteWhereException {
	return forwardEvent(assignment, super.addDeviceStateChange(assignment, request));
    }

    public IEventManagementTenantEngine getTenantEngine() {
	return tenantEngine;
    }

    public void setTenantEngine(IEventManagementTenantEngine tenantEngine) {
	this.tenantEngine = tenantEngine;
    }
}