/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.kafka;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

import com.sitewhere.event.configuration.EventManagementTenantConfiguration;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.common.CommonModelConverter;
import com.sitewhere.grpc.event.EventModelConverter;
import com.sitewhere.grpc.model.DeviceEventModel.GAnyDeviceEventCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GPreprocessedEventPayload;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.kafka.ProcessorSupplierComponent;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

import io.prometheus.client.Counter;

/**
 * Processing supplier that persists events via the event management APIs.
 */
public class EventManagementProcessingSupplier extends ProcessorSupplierComponent<UUID, GPreprocessedEventPayload> {

    /** Counter for processed events */
    private static final Counter PROCESSED_EVENTS = TenantEngineLifecycleComponent
	    .createCounterMetric("inbound_events_event_count", "Count of total events processed by pipeline");

    /** Configuration */
    private EventManagementTenantConfiguration configuration;

    /** Executor service */
    private ExecutorService executor;

    public EventManagementProcessingSupplier(EventManagementTenantConfiguration configuration) {
	this.configuration = configuration;
	this.executor = Executors.newFixedThreadPool(configuration.getProcessingThreadCount());
    }

    public ExecutorService getExecutor() {
	return executor;
    }

    /*
     * @see org.apache.kafka.streams.processor.ProcessorSupplier#get()
     */
    @Override
    public Processor<UUID, GPreprocessedEventPayload> get() {
	return new Processor<UUID, GPreprocessedEventPayload>() {

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
	    public void process(UUID deviceId, GPreprocessedEventPayload event) {
		getExecutor().execute(new PersistenceProcessor(EventManagementProcessingSupplier.this, event));
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
     * Runs event persistence in a separate thread.
     */
    private class PersistenceProcessor extends SystemUserRunnable {

	private GPreprocessedEventPayload event;

	public PersistenceProcessor(ITenantEngineLifecycleComponent component, GPreprocessedEventPayload event) {
	    super(component);
	    this.event = event;
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#runAsSystemUser()
	 */
	@Override
	public void runAsSystemUser() throws SiteWhereException {
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
    }

    protected EventManagementTenantConfiguration getConfiguration() {
	return configuration;
    }
}
