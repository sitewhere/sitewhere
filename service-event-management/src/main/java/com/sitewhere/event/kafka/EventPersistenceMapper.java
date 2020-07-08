/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.streams.KeyValue;

import com.sitewhere.event.configuration.EventManagementTenantConfiguration;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.common.CommonModelConverter;
import com.sitewhere.grpc.event.EventModelConverter;
import com.sitewhere.grpc.model.DeviceEventModel.GAnyDeviceEventCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GPreprocessedEventPayload;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.kafka.KeyValueMapperComponent;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.security.SystemUserCallable;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
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
 * Persists device events via the SiteWhere APIs
 */
public class EventPersistenceMapper
	extends KeyValueMapperComponent<UUID, GPreprocessedEventPayload, KeyValue<UUID, GPreprocessedEventPayload>> {

    /** Counter for processed events */
    private static final Counter PROCESSED_EVENTS = TenantEngineLifecycleComponent
	    .createCounterMetric("inbound_events_event_count", "Count of total events processed by pipeline");

    /** Configuration */
    private EventManagementTenantConfiguration configuration;

    public EventPersistenceMapper(EventManagementTenantConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see org.apache.kafka.streams.kstream.KeyValueMapper#apply(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public KeyValue<UUID, GPreprocessedEventPayload> apply(UUID key, GPreprocessedEventPayload payload) {
	try {
	    new PersistenceProcessor(EventPersistenceMapper.this, payload).call();
	    KeyValue<UUID, GPreprocessedEventPayload> keyValue = KeyValue.pair(key, payload);
	    return keyValue;
	} catch (Exception e) {
	    getLogger().error("Unable to persist device event.", e);
	    return null;
	}
    }

    /**
     * Runs event persistence in system user context.
     */
    private class PersistenceProcessor extends SystemUserCallable<List<? extends IDeviceEvent>> {

	private GPreprocessedEventPayload event;

	public PersistenceProcessor(ITenantEngineLifecycleComponent component, GPreprocessedEventPayload event) {
	    super(component);
	    this.event = event;
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#runAsSystemUser()
	 */
	@Override
	public List<? extends IDeviceEvent> runAsSystemUser() throws SiteWhereException {
	    GAnyDeviceEventCreateRequest grpc = event.getEvent();
	    UUID assignmentId = CommonModelConverter.asApiUuid(event.getDeviceAssignmentId());
	    IDeviceEventCreateRequest request = EventModelConverter.asApiDeviceEventCreateRequest(grpc);
	    switch (request.getEventType()) {
	    case Measurement:
		PROCESSED_EVENTS.labels(buildLabels()).inc();
		return getDeviceEventManagement().addDeviceMeasurements(assignmentId,
			(IDeviceMeasurementCreateRequest) request);
	    case Alert:
		PROCESSED_EVENTS.labels(buildLabels()).inc();
		return getDeviceEventManagement().addDeviceAlerts(assignmentId, (IDeviceAlertCreateRequest) request);
	    case CommandInvocation:
		PROCESSED_EVENTS.labels(buildLabels()).inc();
		return getDeviceEventManagement().addDeviceCommandInvocations(assignmentId,
			(IDeviceCommandInvocationCreateRequest) request);
	    case CommandResponse:
		PROCESSED_EVENTS.labels(buildLabels()).inc();
		return getDeviceEventManagement().addDeviceCommandResponses(assignmentId,
			(IDeviceCommandResponseCreateRequest) request);
	    case Location:
		PROCESSED_EVENTS.labels(buildLabels()).inc();
		return getDeviceEventManagement().addDeviceLocations(assignmentId,
			(IDeviceLocationCreateRequest) request);
	    case StateChange:
		PROCESSED_EVENTS.labels(buildLabels()).inc();
		return getDeviceEventManagement().addDeviceStateChanges(assignmentId,
			(IDeviceStateChangeCreateRequest) request);
	    default:
		getLogger()
			.warn(String.format("Unknown event type sent for storage: %s", request.getEventType().name()));
		return new ArrayList<>();
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
