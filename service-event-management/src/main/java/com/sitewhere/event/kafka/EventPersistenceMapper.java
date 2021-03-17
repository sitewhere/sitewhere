/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.event.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.streams.KeyValue;

import com.sitewhere.event.configuration.EventManagementTenantConfiguration;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.event.EventModelConverter;
import com.sitewhere.grpc.model.DeviceEventModel.GAnyDeviceEventCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GPreprocessedEventPayload;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.kafka.KeyValueMapperComponent;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.security.SystemUserCallable;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.microservice.instance.EventPipelineLogLevel;
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

	    // Log to pipeline event log.
	    logPipelineEvent(payload.getContext().getSourceId().getValue(), payload.getContext().getDeviceToken(),
		    getMicroservice().getIdentifier(),
		    "Persisted " + payload.getEvent().getEventCase().name() + " event for device.", null,
		    EventPipelineLogLevel.Debug);

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
	    IDeviceEventContext context = EventModelConverter.asApiDeviceEventContext(event.getContext());
	    IDeviceEventCreateRequest request = EventModelConverter.asApiDeviceEventCreateRequest(grpc);
	    switch (request.getEventType()) {
	    case Measurement:
		PROCESSED_EVENTS.labels(buildLabels()).inc();
		return getDeviceEventManagement().addDeviceMeasurements(context,
			(IDeviceMeasurementCreateRequest) request);
	    case Alert:
		PROCESSED_EVENTS.labels(buildLabels()).inc();
		return getDeviceEventManagement().addDeviceAlerts(context, (IDeviceAlertCreateRequest) request);
	    case CommandInvocation:
		PROCESSED_EVENTS.labels(buildLabels()).inc();
		return getDeviceEventManagement().addDeviceCommandInvocations(context,
			(IDeviceCommandInvocationCreateRequest) request);
	    case CommandResponse:
		PROCESSED_EVENTS.labels(buildLabels()).inc();
		return getDeviceEventManagement().addDeviceCommandResponses(context,
			(IDeviceCommandResponseCreateRequest) request);
	    case Location:
		PROCESSED_EVENTS.labels(buildLabels()).inc();
		return getDeviceEventManagement().addDeviceLocations(context, (IDeviceLocationCreateRequest) request);
	    case StateChange:
		PROCESSED_EVENTS.labels(buildLabels()).inc();
		return getDeviceEventManagement().addDeviceStateChanges(context,
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
