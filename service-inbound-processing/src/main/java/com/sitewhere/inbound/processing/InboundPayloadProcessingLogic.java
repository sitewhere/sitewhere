/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.processing;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.common.TopicPartition;

import com.sitewhere.grpc.client.event.EventModelMarshaler;
import com.sitewhere.grpc.model.DeviceEventModel.GDecodedEventPayload;
import com.sitewhere.inbound.spi.kafka.IDecodedEventsConsumer;
import com.sitewhere.inbound.spi.kafka.IInboundEventsProducer;
import com.sitewhere.inbound.spi.kafka.IUnregisteredEventsProducer;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingMicroservice;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine;
import com.sitewhere.inbound.spi.processing.IInboundPayloadProcessingLogic;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import io.prometheus.client.Histogram;

/**
 * Processing logic which verifies that an incoming event belongs to a
 * registered device. If the event does not belong to a registered device, it is
 * added to a Kafka topic that can be processed by a registration manager to
 * register the device automatically if so configured. The logic also verifies
 * that an active assignment exists for the device. Finally, the event is sent
 * to a Kafka topic for further processing (such as event persistence).
 */
public class InboundPayloadProcessingLogic extends TenantEngineLifecycleComponent
	implements IInboundPayloadProcessingLogic {

    /** Histogram for device lookup */
    private Histogram deviceLookupTimer;

    /** Histogram for assignment lookup */
    private Histogram assignmentLookupTimer;

    /** Decoded events consumer */
    private IDecodedEventsConsumer decodedEventsConsumer;

    /** Executor service for inbound payload processors */
    private ExecutorService inboundProcessorsExecutor;

    public InboundPayloadProcessingLogic(IDecodedEventsConsumer decodedEventsConsumer) {
	this.decodedEventsConsumer = decodedEventsConsumer;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	if (getInboundProcessorsExecutor() != null) {
	    getInboundProcessorsExecutor().shutdownNow();
	}
	this.inboundProcessorsExecutor = Executors.newFixedThreadPool(
		getDecodedEventsConsumer().getInboundProcessingConfiguration().getProcessingThreadCount());
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getInboundProcessorsExecutor() != null) {
	    getInboundProcessorsExecutor().shutdownNow();
	}
	super.stop(monitor);
    }

    /*
     * @see
     * com.sitewhere.inbound.spi.processing.IInboundPayloadProcessingLogic#process(
     * org.apache.kafka.common.TopicPartition, java.util.List)
     */
    @Override
    public void process(TopicPartition topicPartition, List<GDecodedEventPayload> decoded) throws SiteWhereException {
	for (GDecodedEventPayload event : decoded) {
	    getInboundProcessorsExecutor().execute(new InboundEventPayloadProcessor(event));
	}
    }

    /**
     * Process a single decoded event.
     * 
     * @param event
     * @throws SiteWhereException
     */
    protected void processDecodedEvent(GDecodedEventPayload event) throws SiteWhereException {
	IDeviceAssignment assignment = validateAssignment(event);
	if (assignment != null) {
	    byte[] marshaled = EventModelMarshaler.buildDecodedEventPayloadMessage(event);
	    getInboundEventsProducer().send(event.getDeviceToken(), marshaled);
	}
    }

    /**
     * Validates that inbound event payload references a registered device that is
     * asssigned.
     * 
     * @param payload
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceAssignment validateAssignment(GDecodedEventPayload payload) throws SiteWhereException {
	// Verify that device is registered.
	final Histogram.Timer deviceLookupTime = getDeviceLookupTimer().labels(buildLabels()).startTimer();
	IDevice device = null;
	try {
	    device = getDeviceManagement().getDeviceByToken(payload.getDeviceToken());
	} finally {
	    deviceLookupTime.close();
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

	// Verify that device assignment exists.
	final Histogram.Timer assignmentLookupTime = getAssignmentLookupTimer().labels(buildLabels()).startTimer();
	IDeviceAssignment assignment = null;
	try {
	    assignment = getDeviceManagement().getDeviceAssignment(device.getDeviceAssignmentId());
	} finally {
	    assignmentLookupTime.close();
	}
	if (assignment == null) {
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
    protected void handleUnregisteredDevice(GDecodedEventPayload payload) throws SiteWhereException {
	getLogger().info("Device '" + payload.getDeviceToken()
		+ "' is not registered. Forwarding to unregistered devices topic.");
	byte[] marshaled = EventModelMarshaler.buildDecodedEventPayloadMessage(payload);
	getUnregisteredDeviceEventsProducer().send(payload.getDeviceToken(), marshaled);
    }

    /**
     * Handle case where event is sent for an unassigned device. Forwards
     * information to an out-of-band topic to be processed later.
     * 
     * @param payload
     * @throws SiteWhereException
     */
    protected void handleUnassignedDevice(GDecodedEventPayload payload) throws SiteWhereException {
	getLogger().info("Device '" + payload.getDeviceToken()
		+ "' is not currently assigned. Forwarding to unassigned devices topic.");
	byte[] marshaled = EventModelMarshaler.buildDecodedEventPayloadMessage(payload);
	getUnregisteredDeviceEventsProducer().send(payload.getDeviceToken(), marshaled);
    }

    /**
     * Processor that unmarshals a decoded event and forwards it for registration
     * verification.
     * 
     * @author Derek
     */
    protected class InboundEventPayloadProcessor extends SystemUserRunnable {

	/** Event to be processed */
	private GDecodedEventPayload event;

	public InboundEventPayloadProcessor(GDecodedEventPayload event) {
	    super(getTenantEngine().getMicroservice(), getTenantEngine().getTenant());
	    this.event = event;
	}

	@Override
	public void runAsSystemUser() throws SiteWhereException {
	    processDecodedEvent(event);
	}
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
	return ((IInboundProcessingMicroservice) getTenantEngine().getMicroservice()).getDeviceManagementApiChannel();
    }

    /**
     * Get inbound events Kafka producer.
     * 
     * @return
     */
    protected IInboundEventsProducer getInboundEventsProducer() {
	return ((IInboundProcessingTenantEngine) getTenantEngine()).getInboundEventsProducer();
    }

    protected ExecutorService getInboundProcessorsExecutor() {
	return inboundProcessorsExecutor;
    }

    protected IDecodedEventsConsumer getDecodedEventsConsumer() {
	return decodedEventsConsumer;
    }

    protected Histogram getDeviceLookupTimer() {
	return deviceLookupTimer;
    }

    protected Histogram getAssignmentLookupTimer() {
	return assignmentLookupTimer;
    }
}