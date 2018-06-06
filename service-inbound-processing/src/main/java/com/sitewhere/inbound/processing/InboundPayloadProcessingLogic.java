/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.processing;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.sitewhere.common.MarshalUtils;
import com.sitewhere.grpc.client.event.BlockingDeviceEventManagement;
import com.sitewhere.grpc.kafka.model.KafkaModel.GInboundEventPayload;
import com.sitewhere.grpc.model.converter.KafkaModelConverter;
import com.sitewhere.grpc.model.marshaler.KafkaModelMarshaler;
import com.sitewhere.inbound.spi.kafka.IUnregisteredEventsProducer;
import com.sitewhere.inbound.spi.microservice.IInboundEventStorageStrategy;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingMicroservice;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine;
import com.sitewhere.rest.model.microservice.kafka.payload.InboundEventPayload;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

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
public class InboundPayloadProcessingLogic extends TenantEngineLifecycleComponent {

    /** Meter for counting processed events */
    private Meter processedEvents;

    /** Meter for counting failed events */
    private Meter failedEvents;

    /** Histogram for device lookup */
    private Timer deviceLookupTimer;

    /** Histogram for assignment lookup */
    private Timer assignmentLookupTimer;

    /** Histogram for event storage time */
    private Timer eventStorageTimer;

    /** Event storage strategy */
    private IInboundEventStorageStrategy eventStorageStrategy;

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Set up metrics.
	this.processedEvents = createMeterMetric("processedEvents");
	this.failedEvents = createMeterMetric("failedEvents");
	this.deviceLookupTimer = createTimerMetric("deviceLookup");
	this.assignmentLookupTimer = createTimerMetric("assignmentLookup");
	this.eventStorageTimer = createTimerMetric("eventStorage");
	this.eventStorageStrategy = new UnaryEventStorageStrategy((IInboundProcessingTenantEngine) getTenantEngine(),
		this);
    }

    /**
     * Process a batch of inbound event records.
     * 
     * @param records
     * @throws SiteWhereException
     */
    public void process(List<ConsumerRecord<String, byte[]>> records) throws SiteWhereException {
	processPayloads(records);
    }

    /**
     * Build requests based on batch of Kafka records.
     * 
     * @param records
     * @return
     */
    protected void processPayloads(List<ConsumerRecord<String, byte[]>> records) throws SiteWhereException {
	for (ConsumerRecord<String, byte[]> record : records) {
	    GInboundEventPayload payload = decodeRequest(record);
	    IDeviceAssignment assignment = validateAssignment(payload);
	    final Timer.Context eventStorageTime = getEventStorageTimer().time();
	    try {
		getEventStorageStrategy().storeDeviceEvent(assignment, payload);
	    } finally {
		eventStorageTime.stop();
	    }
	}
    }

    /**
     * Process an inbound payload into an assignment event create request.
     * 
     * @param record
     * @return
     * @throws SiteWhereException
     */
    protected GInboundEventPayload decodeRequest(ConsumerRecord<String, byte[]> record) throws SiteWhereException {
	GInboundEventPayload message = KafkaModelMarshaler.parseInboundEventPayloadMessage(record.value());
	if (getLogger().isDebugEnabled()) {
	    InboundEventPayload payload = KafkaModelConverter.asApiInboundEventPayload(message);
	    getLogger().debug("Received decoded event payload:\n\n" + MarshalUtils.marshalJsonAsPrettyString(payload));
	}
	return message;
    }

    /**
     * Validates that inbound event payload references a registered device that is
     * asssigned.
     * 
     * @param payload
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceAssignment validateAssignment(GInboundEventPayload payload) throws SiteWhereException {
	// Verify that device is registered.
	final Timer.Context deviceLookupTime = getDeviceLookupTimer().time();
	IDevice device = null;
	try {
	    device = getDeviceManagement().getDeviceByToken(payload.getDeviceToken());
	} finally {
	    deviceLookupTime.stop();
	}
	if (device == null) {
	    handleUnregisteredDevice(payload);
	    return null;
	}

	// Verify that device is assigned.
	if (device.getDeviceAssignmentId() == null) {
	    handleUnassignedDevice(payload);
	    return null;
	}

	// Verify that device is assigned.
	final Timer.Context assignmentLookupTime = getAssignmentLookupTimer().time();
	IDeviceAssignment assignment = null;
	try {
	    assignment = getDeviceManagement().getDeviceAssignment(device.getDeviceAssignmentId());
	} finally {
	    assignmentLookupTime.stop();
	}
	if (assignment == null) {
	    getLogger().info("Assignment information for " + payload.getDeviceToken() + " is invalid.");
	    handleUnassignedDevice(payload);
	    return null;
	}

	return assignment;
    }

    /**
     * Handle case where event is processed for an unregistered device. Forwards
     * information to an out-of-band topic to be processed later.
     * 
     * @param payload
     * @throws SiteWhereException
     */
    protected void handleUnregisteredDevice(GInboundEventPayload payload) throws SiteWhereException {
	getLogger().info("Device '" + payload.getDeviceToken()
		+ "' is not registered. Forwarding to unregistered devices topic.");
	byte[] marshaled = KafkaModelMarshaler.buildInboundEventPayloadMessage(payload);
	getUnregisteredDeviceEventsProducer().send(payload.getDeviceToken(), marshaled);
    }

    /**
     * Handle case where event is sent for an unassigned device. Forwards
     * information to an out-of-band topic to be processed later.
     * 
     * @param payload
     * @throws SiteWhereException
     */
    protected void handleUnassignedDevice(GInboundEventPayload payload) throws SiteWhereException {
	getLogger().info("Device '" + payload.getDeviceToken()
		+ "' is not currently assigned. Forwarding to unassigned devices topic.");
	byte[] marshaled = KafkaModelMarshaler.buildInboundEventPayloadMessage(payload);
	getUnregisteredDeviceEventsProducer().send(payload.getDeviceToken(), marshaled);
    }

    /**
     * Get Kafka producer for unregistered device events.
     * 
     * @return
     */
    protected IUnregisteredEventsProducer getUnregisteredDeviceEventsProducer() {
	return ((IInboundProcessingTenantEngine) getTenantEngine()).getUnregisteredDeviceEventsProducer();
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

    public Meter getProcessedEvents() {
	return processedEvents;
    }

    public Meter getFailedEvents() {
	return failedEvents;
    }

    public Timer getEventStorageTimer() {
	return eventStorageTimer;
    }

    public Timer getDeviceLookupTimer() {
	return deviceLookupTimer;
    }

    public Timer getAssignmentLookupTimer() {
	return assignmentLookupTimer;
    }

    public IInboundEventStorageStrategy getEventStorageStrategy() {
	return eventStorageStrategy;
    }
}