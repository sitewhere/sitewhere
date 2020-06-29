/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.processing;

import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.model.DeviceEventModel.GAnyDeviceEventCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GPreprocessedEventPayload;
import com.sitewhere.event.spi.kafka.IInboundEventsConsumer;
import com.sitewhere.event.spi.processing.IEventManagementStoreLogic;
import com.sitewhere.grpc.client.common.converter.CommonModelConverter;
import com.sitewhere.grpc.client.event.EventModelConverter;
import com.sitewhere.microservice.kafka.InboundEventsConsumer;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.request.*;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import org.apache.kafka.common.TopicPartition;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Processing logic which
 */
public class EventManagementStoreLogic extends TenantEngineLifecycleComponent
	implements IEventManagementStoreLogic {

    /** Histogram for device lookup */
    private static final Histogram DEVICE_LOOKUP_TIMER = TenantEngineLifecycleComponent
	    .createHistogramMetric("inbound_device_lookup_timer", "Timer for device lookup on inbound events");

    /** Histogram for assignment lookup */
    private static final Histogram ASSIGNMENT_LOOKUP_TIMER = TenantEngineLifecycleComponent
	    .createHistogramMetric("inbound_aaignment_lookup_timer", "Timer for assignment lookup on inbound events");


	/** Counter for processed events */
	private static final Gauge KAFFA_BATCH_SIZE = TenantEngineLifecycleComponent
			.createGaugeMetric("inbound_events_kafka_batch_size", "Size of Kafka inbound event batches");

	/** Counter for processed events */
	private static final Counter PROCESSED_EVENTS = TenantEngineLifecycleComponent
			.createCounterMetric("inbound_events_event_count", "Count of total events processed by consumer");

    /** inbount events consumer */
    private InboundEventsConsumer inboundEventsConsumer;


    /** Executor service for event management processors */
	private ExecutorService eventManagementExecutor;

	public EventManagementStoreLogic(InboundEventsConsumer inboundEventsConsumer) {
	this.inboundEventsConsumer = inboundEventsConsumer;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	if (getEventManagementExecutor() != null) {
		getEventManagementExecutor().shutdownNow();
	}
//	this.eventManagementExecutor = Executors.newFixedThreadPool(
//			getInboundEventsConsumer().getEventManagementConfiguration().getProcessingThreadCount());
//    }
	this.eventManagementExecutor = Executors.newFixedThreadPool(25);
	}

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getEventManagementExecutor() != null) {
		getEventManagementExecutor().shutdownNow();
	}
	super.stop(monitor);
    }

    /*
     * @see
     * com.sitewhere.inbound.spi.processing.IInboundPayloadProcessingLogic#process(
     * org.apache.kafka.common.TopicPartition, java.util.List)
     */
    @Override
    public void process(TopicPartition topicPartition, List<GPreprocessedEventPayload> decoded) throws SiteWhereException {
		KAFFA_BATCH_SIZE.labels(buildLabels()).set(decoded.size());
    	for (GPreprocessedEventPayload event : decoded) {
		//getEventManagementExecutor().execute(new InboundEventPayloadProcessor(event));
			getEventManagementExecutor().execute(new EventManagementPayloadProcessor(event));
		}
    }

	/**
	 * Process a single store event.
	 *
	 * @param event
	 * @throws SiteWhereException
	 */
	protected void processStoreEvents(GPreprocessedEventPayload event) throws SiteWhereException{
		try {
			GAnyDeviceEventCreateRequest grpc = event.getEvent();
			UUID assignmentId = CommonModelConverter.asApiUuid(event.getDeviceAssignmentId());
			IDeviceEventCreateRequest request = EventModelConverter.asApiDeviceEventCreateRequest(grpc);
			switch (request.getEventType()) {
				case Measurement:
					getDeviceEventManagement().addDeviceMeasurements(assignmentId,
							(IDeviceMeasurementCreateRequest) request);
					break;
				case Alert:
					getDeviceEventManagement().addDeviceAlerts(assignmentId, (IDeviceAlertCreateRequest) request);
					break;
				case CommandInvocation:
					getDeviceEventManagement().addDeviceCommandInvocations(assignmentId,
							(IDeviceCommandInvocationCreateRequest) request);
					break;
				case CommandResponse:
					getDeviceEventManagement().addDeviceCommandResponses(assignmentId,
							(IDeviceCommandResponseCreateRequest) request);
					break;
				case Location:
					getDeviceEventManagement().addDeviceLocations(assignmentId, (IDeviceLocationCreateRequest) request);
					break;
				case StateChange:
					getDeviceEventManagement().addDeviceStateChanges(assignmentId,
							(IDeviceStateChangeCreateRequest) request);
					break;
				default:
					getLogger().warn(
							String.format("Unknown event type sent for storage: %s", request.getEventType().name()));
			}

			// Keep metrics on processed events.
			PROCESSED_EVENTS.labels(buildLabels()).inc();
		} catch (SiteWhereException e) {
			getLogger().error("Unable to process inbound event payload.", e);
		} catch (Throwable e) {
			getLogger().error("Unhandled exception processing inbound event payload.", e);
		}
	}

	protected IDeviceEventManagement getDeviceEventManagement() {
		return ((IEventManagementTenantEngine) getTenantEngine()).getEventManagement();
	}

    /**
     * Processor that unmarshals a decoded event and forwards it for preprocessed event.
     * 
     * @author Derek
     */
    protected class EventManagementPayloadProcessor extends SystemUserRunnable {

	/** Event to be processed */
	private GPreprocessedEventPayload event;

	public EventManagementPayloadProcessor(GPreprocessedEventPayload event) {
	    super(getTenantEngine().getMicroservice(), getTenantEngine().getTenant());
	    this.event = event;
	}

	@Override
	public void runAsSystemUser() throws SiteWhereException {
	    //processDecodedEvent(event);
		processStoreEvents(event);

	}
    }

    protected ExecutorService getEventManagementExecutor() {
	return eventManagementExecutor;
    }

    protected IInboundEventsConsumer getInboundEventsConsumer() {
	return inboundEventsConsumer;
    }
}
