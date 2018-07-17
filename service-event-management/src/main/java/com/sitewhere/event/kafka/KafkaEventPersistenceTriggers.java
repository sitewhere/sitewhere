/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.kafka;

import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.event.DeviceEventManagementDecorator;
import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.client.event.EventModelConverter;
import com.sitewhere.grpc.client.event.EventModelMarshaler;
import com.sitewhere.grpc.model.DeviceEventModel.GPersistedEventPayload;
import com.sitewhere.rest.model.microservice.kafka.payload.PersistedEventPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;

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
     * @param deviceAssignmentId
     * @param event
     * @return
     * @throws SiteWhereException
     */
    protected <T extends IDeviceEvent> List<T> forwardEvents(UUID deviceAssignmentId, List<T> events)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
	for (T event : events) {
	    PersistedEventPayload api = new PersistedEventPayload();
	    api.setDeviceId(assignment.getDeviceId());
	    api.setEvent(event);
	    GPersistedEventPayload payload = EventModelConverter.asGrpcPersistedEventPayload(api);

	    getTenantEngine().getInboundPersistedEventsProducer().send(assignment.getId().toString(),
		    EventModelMarshaler.buildPersistedEventPayloadMessage(payload));
	}
	return events;
    }

    /*
     * @see
     * com.sitewhere.event.DeviceEventManagementDecorator#addDeviceMeasurements(java
     * .util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest[])
     */
    @Override
    public List<IDeviceMeasurement> addDeviceMeasurements(UUID deviceAssignmentId,
	    IDeviceMeasurementCreateRequest... measurements) throws SiteWhereException {
	return forwardEvents(deviceAssignmentId, super.addDeviceMeasurements(deviceAssignmentId, measurements));
    }

    /*
     * @see
     * com.sitewhere.event.DeviceEventManagementDecorator#addDeviceLocations(java.
     * util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest[])
     */
    @Override
    public List<IDeviceLocation> addDeviceLocations(UUID deviceAssignmentId, IDeviceLocationCreateRequest... request)
	    throws SiteWhereException {
	return forwardEvents(deviceAssignmentId, super.addDeviceLocations(deviceAssignmentId, request));
    }

    /*
     * @see
     * com.sitewhere.event.DeviceEventManagementDecorator#addDeviceAlerts(java.util.
     * UUID, com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest[])
     */
    @Override
    public List<IDeviceAlert> addDeviceAlerts(UUID deviceAssignmentId, IDeviceAlertCreateRequest... request)
	    throws SiteWhereException {
	return forwardEvents(deviceAssignmentId, super.addDeviceAlerts(deviceAssignmentId, request));
    }

    /*
     * @see com.sitewhere.event.DeviceEventManagementDecorator#
     * addDeviceCommandInvocations(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest[
     * ])
     */
    @Override
    public List<IDeviceCommandInvocation> addDeviceCommandInvocations(UUID deviceAssignmentId,
	    IDeviceCommandInvocationCreateRequest... request) throws SiteWhereException {
	return forwardEvents(deviceAssignmentId, super.addDeviceCommandInvocations(deviceAssignmentId, request));
    }

    /*
     * @see
     * com.sitewhere.event.DeviceEventManagementDecorator#addDeviceCommandResponses(
     * java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest[])
     */
    @Override
    public List<IDeviceCommandResponse> addDeviceCommandResponses(UUID deviceAssignmentId,
	    IDeviceCommandResponseCreateRequest... request) throws SiteWhereException {
	return forwardEvents(deviceAssignmentId, super.addDeviceCommandResponses(deviceAssignmentId, request));
    }

    /*
     * @see
     * com.sitewhere.event.DeviceEventManagementDecorator#addDeviceStateChanges(java
     * .util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest[])
     */
    @Override
    public List<IDeviceStateChange> addDeviceStateChanges(UUID deviceAssignmentId,
	    IDeviceStateChangeCreateRequest... request) throws SiteWhereException {
	return forwardEvents(deviceAssignmentId, super.addDeviceStateChanges(deviceAssignmentId, request));
    }

    /**
     * Assert that a device assignment exists and throw an exception if not.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceAssignment assertDeviceAssignmentById(UUID id) throws SiteWhereException {
	IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignment(id);
	if (assignment == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentId, ErrorLevel.ERROR);
	}
	return assignment;
    }

    protected IDeviceManagement getDeviceManagement() {
	return ((IEventManagementMicroservice) getTenantEngine().getMicroservice()).getDeviceManagementApiDemux()
		.getApiChannel();
    }

    public IEventManagementTenantEngine getTenantEngine() {
	return tenantEngine;
    }

    public void setTenantEngine(IEventManagementTenantEngine tenantEngine) {
	this.tenantEngine = tenantEngine;
    }
}