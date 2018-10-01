/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.processing;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.sitewhere.common.MarshalUtils;
import com.sitewhere.grpc.client.event.BlockingDeviceEventManagement;
import com.sitewhere.grpc.client.event.EventModelConverter;
import com.sitewhere.grpc.client.event.EventModelMarshaler;
import com.sitewhere.grpc.model.DeviceEventModel.GInboundEventPayload;
import com.sitewhere.inbound.spi.kafka.IDecodedEventsConsumer;
import com.sitewhere.inbound.spi.kafka.IUnregisteredEventsProducer;
import com.sitewhere.inbound.spi.microservice.IInboundEventStorageStrategy;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingMicroservice;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine;
import com.sitewhere.inbound.spi.processing.IInboundPayloadProcessingLogic;
import com.sitewhere.microservice.security.SystemUserCallable;
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
public class InboundPayloadProcessingLogic extends TenantEngineLifecycleComponent
	implements IInboundPayloadProcessingLogic {

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

    /** Decoded events consumer */
    private IDecodedEventsConsumer decodedEventsConsumer;

    /** Event storage strategy */
    private IInboundEventStorageStrategy eventStorageStrategy;

    /** Executor service for inbound payload processors */
    private ExecutorService inboundProcessorsExecutor;

    public InboundPayloadProcessingLogic(IDecodedEventsConsumer decodedEventsConsumer) {
	this.decodedEventsConsumer = decodedEventsConsumer;
    }

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
    public void process(TopicPartition topicPartition, List<ConsumerRecord<String, byte[]>> records)
	    throws SiteWhereException {
	long start = System.currentTimeMillis();
	CompletionService<ConsumerRecord<String, byte[]>> completionService = new ExecutorCompletionService<ConsumerRecord<String, byte[]>>(
		getInboundProcessorsExecutor());
	for (ConsumerRecord<String, byte[]> record : records) {
	    completionService.submit(new InboundEventPayloadProcessor(record));
	}
	for (int i = 0; i < records.size(); i++) {
	    try {
		completionService.take().get();
	    } catch (ExecutionException e) {
		throw new SiteWhereException("Exception processing inbound event.", e.getCause());
	    } catch (InterruptedException e) {
		throw new SiteWhereException("Interrupted while waiting on inbound record to process.", e);
	    }
	}
	getLogger().info("Stored " + records.size() + " records in " + (System.currentTimeMillis() - start) + "ms.");
    }

    /**
     * Process a single record.
     * 
     * @param record
     * @throws SiteWhereException
     */
    protected void processRecord(ConsumerRecord<String, byte[]> record) throws SiteWhereException {
	GInboundEventPayload payload = decodeRequest(record);
	IDeviceAssignment assignment = validateAssignment(payload);
	if (assignment != null) {
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
	GInboundEventPayload message = EventModelMarshaler.parseInboundEventPayloadMessage(record.value());
	if (getLogger().isDebugEnabled()) {
	    InboundEventPayload payload = EventModelConverter.asApiInboundEventPayload(message);
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

	// Verify that device assignment exists.
	final Timer.Context assignmentLookupTime = getAssignmentLookupTimer().time();
	IDeviceAssignment assignment = null;
	try {
	    assignment = getDeviceManagement().getDeviceAssignment(device.getDeviceAssignmentId());
	} finally {
	    assignmentLookupTime.stop();
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
    protected void handleUnregisteredDevice(GInboundEventPayload payload) throws SiteWhereException {
	getLogger().info("Device '" + payload.getDeviceToken()
		+ "' is not registered. Forwarding to unregistered devices topic.");
	byte[] marshaled = EventModelMarshaler.buildInboundEventPayloadMessage(payload);
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
	byte[] marshaled = EventModelMarshaler.buildInboundEventPayloadMessage(payload);
	getUnregisteredDeviceEventsProducer().send(payload.getDeviceToken(), marshaled);
    }

    /**
     * Processor that unmarshals a decoded event and forwards it for registration
     * verification.
     * 
     * @author Derek
     */
    protected class InboundEventPayloadProcessor extends SystemUserCallable<ConsumerRecord<String, byte[]>> {

	/** Record to be processed */
	private ConsumerRecord<String, byte[]> record;

	public InboundEventPayloadProcessor(ConsumerRecord<String, byte[]> record) {
	    super(getTenantEngine().getMicroservice(), getTenantEngine().getTenant());
	    this.record = record;
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#
	 * runAsSystemUser()
	 */
	@Override
	public ConsumerRecord<String, byte[]> runAsSystemUser() throws SiteWhereException {
	    processRecord(getRecord());
	    return getRecord();
	}

	protected ConsumerRecord<String, byte[]> getRecord() {
	    return record;
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

    protected ExecutorService getInboundProcessorsExecutor() {
	return inboundProcessorsExecutor;
    }

    protected IDecodedEventsConsumer getDecodedEventsConsumer() {
	return decodedEventsConsumer;
    }

    protected Meter getProcessedEvents() {
	return processedEvents;
    }

    protected Meter getFailedEvents() {
	return failedEvents;
    }

    protected Timer getEventStorageTimer() {
	return eventStorageTimer;
    }

    protected Timer getDeviceLookupTimer() {
	return deviceLookupTimer;
    }

    protected Timer getAssignmentLookupTimer() {
	return assignmentLookupTimer;
    }

    protected IInboundEventStorageStrategy getEventStorageStrategy() {
	return eventStorageStrategy;
    }
}