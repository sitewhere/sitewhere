/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.kafka;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

import com.sitewhere.grpc.common.CommonModelConverter;
import com.sitewhere.grpc.event.EventModelMarshaler;
import com.sitewhere.grpc.model.DeviceEventModel.GDecodedEventPayload;
import com.sitewhere.grpc.model.DeviceEventModel.GPreprocessedEventPayload;
import com.sitewhere.inbound.spi.kafka.IInboundEventsProducer;
import com.sitewhere.inbound.spi.kafka.IUnregisteredEventsProducer;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingMicroservice;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine;
import com.sitewhere.inbound.spi.processing.IInboundProcessingConfiguration;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.kafka.ProcessorSupplierComponent;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

import io.prometheus.client.Histogram;

/**
 * Processing logic which verifies that an incoming event belongs to a
 * registered device. If the event does not belong to a registered device, it is
 * added to a Kafka topic that can be processed by a registration manager to
 * register the device automatically if so configured. The logic also verifies
 * that an active assignment exists for the device. Finally, the event is sent
 * to a Kafka topic for further processing (such as event persistence).
 */
public class InboundEventProcessingSupplier extends ProcessorSupplierComponent<String, GDecodedEventPayload> {

    /** Histogram for device lookup */
    private static final Histogram DEVICE_LOOKUP_TIMER = TenantEngineLifecycleComponent
	    .createHistogramMetric("inbound_device_lookup_timer", "Timer for device lookup on inbound events");

    /** Histogram for assignment lookup */
    private static final Histogram ASSIGNMENT_LOOKUP_TIMER = TenantEngineLifecycleComponent
	    .createHistogramMetric("inbound_assignment_lookup_timer", "Timer for assignment lookup on inbound events");

    /** Configuration */
    private IInboundProcessingConfiguration configuration;

    /** Executor service */
    private ExecutorService executor;

    public InboundEventProcessingSupplier(IInboundProcessingConfiguration configuration) {
	this.configuration = configuration;
	this.executor = Executors.newFixedThreadPool(configuration.getProcessingThreadCount());
    }

    public IInboundProcessingConfiguration getConfiguration() {
	return configuration;
    }

    public ExecutorService getExecutor() {
	return executor;
    }

    /*
     * @see org.apache.kafka.streams.processor.ProcessorSupplier#get()
     */
    @Override
    public Processor<String, GDecodedEventPayload> get() {
	return new Processor<String, GDecodedEventPayload>() {

	    @SuppressWarnings("unused")
	    private ProcessorContext context;

	    /*
	     * @see
	     * org.apache.kafka.streams.processor.Processor#init(org.apache.kafka.streams.
	     * processor.ProcessorContext)
	     */
	    @Override
	    public void init(ProcessorContext context) {
		this.context = context;
	    }

	    /*
	     * @see org.apache.kafka.streams.processor.Processor#process(java.lang.Object,
	     * java.lang.Object)
	     */
	    @Override
	    public void process(String key, GDecodedEventPayload event) {
		getExecutor().execute(new InboundEventProcessor(InboundEventProcessingSupplier.this, event));
	    }

	    /*
	     * @see org.apache.kafka.streams.processor.Processor#close()
	     */
	    @Override
	    public void close() {
	    }
	};
    }

    /**
     * Runs event processing in a separate thread.
     */
    private class InboundEventProcessor extends SystemUserRunnable {

	private GDecodedEventPayload event;

	public InboundEventProcessor(ITenantEngineLifecycleComponent component, GDecodedEventPayload event) {
	    super(component);
	    this.event = event;
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#runAsSystemUser()
	 */
	@Override
	public void runAsSystemUser() throws SiteWhereException {
	    try {
		List<? extends IDeviceAssignment> assignments = validateAssignment(event);
		if (getLogger().isDebugEnabled()) {
		    getLogger()
			    .debug(String
				    .format("Found %s for '%s'.",
					    assignments.size() > 1 ? "" + assignments.size() + " active assignments"
						    : "" + assignments.size() + " active assignment",
					    event.getDeviceToken()));
		}
		// Loop through assignments and send an enriched payload for each.
		if (assignments != null) {
		    for (IDeviceAssignment assignment : assignments) {
			GPreprocessedEventPayload preproc = buildPreProcessedEventPayload(assignment, event);
			byte[] marshaled = EventModelMarshaler.buildPreprocessedEventPayloadMessage(preproc);
			if (getLogger().isDebugEnabled()) {
			    getLogger().debug(String.format(
				    "Forwarding preprocessed payload for '%s' to Kafka for further processing.",
				    event.getDeviceToken()));
			}
			getInboundEventsProducer().send(event.getDeviceToken(), marshaled);
		    }
		}
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to process inbound event payload.", e);
	    } catch (Throwable e) {
		getLogger().error("Unhandled exception processing inbound event payload.", e);
	    }
	}

	/**
	 * Build preprocessed payload by adding assignment details to decoded event.
	 * 
	 * @param assignment
	 * @param event
	 * @return
	 * @throws SiteWhereException
	 */
	protected GPreprocessedEventPayload buildPreProcessedEventPayload(IDeviceAssignment assignment,
		GDecodedEventPayload event) throws SiteWhereException {
	    GPreprocessedEventPayload.Builder preproc = GPreprocessedEventPayload.newBuilder();
	    preproc.setSourceId(event.getSourceId());
	    preproc.setDeviceToken(event.getDeviceToken());
	    preproc.setEvent(event.getEvent());
	    preproc.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(assignment.getId()));
	    preproc.setDeviceId(CommonModelConverter.asGrpcUuid(assignment.getDeviceId()));
	    return preproc.build();
	}

	/**
	 * Validates that inbound event payload references a registered device that has
	 * one or more active assignments.
	 * 
	 * @param payload
	 * @return
	 * @throws SiteWhereException
	 */
	protected List<? extends IDeviceAssignment> validateAssignment(GDecodedEventPayload payload)
		throws SiteWhereException {
	    if (getLogger().isDebugEnabled()) {
		getLogger().debug(String.format("Validating device assignment for '%s'.", payload.getDeviceToken()));
	    }

	    // Verify that device is registered.
	    final Histogram.Timer deviceLookupTime = DEVICE_LOOKUP_TIMER.labels(getTenantEngine().buildLabels())
		    .startTimer();
	    IDevice device = null;
	    try {
		device = getDeviceManagement().getDeviceByToken(payload.getDeviceToken());
	    } finally {
		deviceLookupTime.close();
	    }
	    if (device == null) {
		if (getLogger().isDebugEnabled()) {
		    getLogger().debug(String.format("Device not found for token '%s'.", payload.getDeviceToken()));
		}
		handleUnregisteredDevice(payload);
		return null;
	    }

	    // Verify that device assignment exists.
	    final Histogram.Timer assignmentLookupTime = ASSIGNMENT_LOOKUP_TIMER.labels(getTenantEngine().buildLabels())
		    .startTimer();
	    List<? extends IDeviceAssignment> assignments = null;
	    try {
		assignments = getDeviceManagement().getActiveDeviceAssignments(device.getId());
	    } finally {
		assignmentLookupTime.close();
	    }
	    if (assignments.size() == 0) {
		handleUnassignedDevice(payload);
		return null;
	    }

	    return assignments;
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
	    return ((IInboundProcessingMicroservice) getTenantEngine().getMicroservice()).getDeviceManagement();
	}

	/**
	 * Get inbound events Kafka producer.
	 * 
	 * @return
	 */
	protected IInboundEventsProducer getInboundEventsProducer() {
	    return ((IInboundProcessingTenantEngine) getTenantEngine()).getInboundEventsProducer();
	}
    }
}
