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
package com.sitewhere.sources.manager;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.inject.Inject;
import com.sitewhere.grpc.device.DeviceModelMarshaler;
import com.sitewhere.grpc.event.EventModelMarshaler;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.rest.model.device.event.kafka.DecodedEventPayload;
import com.sitewhere.rest.model.device.event.kafka.DeviceRegistrationPayload;
import com.sitewhere.sources.configuration.EventSourcesTenantConfiguration;
import com.sitewhere.sources.kafka.DecodedEventsProducer;
import com.sitewhere.sources.kafka.DeviceRegistrationEventsProducer;
import com.sitewhere.sources.kafka.FailedDecodeEventsProducer;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IEventSourcesManager;
import com.sitewhere.sources.spi.IInboundEventSource;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.microservice.instance.EventPipelineLogLevel;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleStatus;

/**
 * Manages lifecycle of the list of event sources configured for a tenant.
 */
public class EventSourcesManager extends TenantEngineLifecycleComponent implements IEventSourcesManager {

    /** Event sources configuration */
    private EventSourcesTenantConfiguration configuration;

    /** List of event sources */
    private List<IInboundEventSource<?>> eventSources;

    /** Kafka producer for decoded events from event sources */
    private DecodedEventsProducer decodedEventsProducer;

    /** Kafka producer for events that could not be decoded */
    private FailedDecodeEventsProducer failedDecodeEventsProducer;

    /** Kafka producer for device registation events from event sources */
    private DeviceRegistrationEventsProducer deviceRegistrationEventsProducer;

    @Inject
    public EventSourcesManager(EventSourcesTenantConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getLogger().info(String.format("About to initialize event sources manager with configuration:\n%s\n\n",
		MarshalUtils.marshalJsonAsPrettyString(getConfiguration().getEventSources())));
	this.eventSources = EventSourcesParser.parse(this, getConfiguration());

	// Create Kafka components.
	createKafkaComponents();

	// Composite step for initializing component.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize decoded events producer.
	init.addInitializeStep(this, getDecodedEventsProducer(), true);

	// Initialize failed decode events producer.
	init.addInitializeStep(this, getFailedDecodeEventsProducer(), true);

	// Initialize device registration events producer.
	init.addInitializeStep(this, getDeviceRegistrationEventsProducer(), true);

	// Initialize event sources.
	for (IInboundEventSource<?> source : getEventSources()) {
	    init.addInitializeStep(this, source, false);
	}

	// Execute initialization steps.
	init.execute(monitor);
    }

    /**
     * Create Kafka components.
     * 
     * @throws SiteWhereException
     */
    protected void createKafkaComponents() throws SiteWhereException {
	this.decodedEventsProducer = new DecodedEventsProducer();
	this.failedDecodeEventsProducer = new FailedDecodeEventsProducer();
	this.deviceRegistrationEventsProducer = new DeviceRegistrationEventsProducer();
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for starting component.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start decoded events producer.
	start.addStartStep(this, getDecodedEventsProducer(), true);

	// Start failed decode events producer.
	start.addStartStep(this, getFailedDecodeEventsProducer(), true);

	// Start device registration events producer.
	start.addStartStep(this, getDeviceRegistrationEventsProducer(), true);

	// Start event sources.
	for (IInboundEventSource<?> source : getEventSources()) {
	    start.addStartStep(this, source, false);
	}

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for stopping component.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop event sources.
	for (IInboundEventSource<?> source : getEventSources()) {
	    stop.addStopStep(this, source);
	}

	// Stop device registration events producer.
	stop.addStopStep(this, getDeviceRegistrationEventsProducer());

	// Stop failed decode events producer.
	stop.addStopStep(this, getFailedDecodeEventsProducer());

	// Stop decoded events producer.
	stop.addStopStep(this, getDecodedEventsProducer());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see com.sitewhere.sources.spi.IEventSourcesManager#handleDecodedEvent(java.
     * lang.String, byte[], java.util.Map,
     * com.sitewhere.spi.device.communication.IDecodedDeviceRequest)
     */
    @Override
    public void handleDecodedEvent(String sourceId, byte[] encoded, Map<String, Object> metadata,
	    IDecodedDeviceRequest<?> decoded) throws SiteWhereException {
	if (getLogger().isDebugEnabled()) {
	    getLogger().debug("Processing decoded event...");
	}
	String sourceUnique = sourceId + ":" + UUID.randomUUID().toString();
	if (decoded.getRequest() instanceof IDeviceEventCreateRequest) {
	    if (getLogger().isDebugEnabled()) {
		getLogger().debug("Forwarding decoded event create request to Kafka outbound topic.");
	    }
	    DecodedEventPayload payload = new DecodedEventPayload();
	    payload.setSourceId(sourceUnique);
	    payload.setDeviceToken(decoded.getDeviceToken());
	    payload.setOriginator(decoded.getOriginator());
	    payload.setEventCreateRequest((IDeviceEventCreateRequest) decoded.getRequest());
	    if (getDecodedEventsProducer().getLifecycleStatus() == LifecycleStatus.Started) {
		// Build and forward inbound event payload message.
		getDecodedEventsProducer().send(decoded.getDeviceToken(),
			EventModelMarshaler.buildDecodedEventPayloadMessage(payload));
		logPipelineEvent(sourceUnique, payload.getDeviceToken(), getMicroservice().getIdentifier(),
			"Decoded event create request and forwarded to Kafka decoded events topic.", null,
			EventPipelineLogLevel.Debug);
	    } else {
		getLogger().warn("Producer not started. Unable to add decoded event to topic.");
		logPipelineEvent(sourceUnique, payload.getDeviceToken(), getMicroservice().getIdentifier(),
			"Kafka producer was not started. Unable to forward event.", null, EventPipelineLogLevel.Debug);
	    }
	} else if (decoded.getRequest() instanceof IDeviceRegistrationRequest) {
	    if (getLogger().isDebugEnabled()) {
		getLogger().debug("Forwarding decoded registration request to Kafka outbound topic.");
	    }
	    DeviceRegistrationPayload payload = new DeviceRegistrationPayload();
	    payload.setSourceId(sourceUnique);
	    payload.setDeviceToken(decoded.getDeviceToken());
	    payload.setOriginator(decoded.getOriginator());
	    payload.setDeviceRegistrationRequest((IDeviceRegistrationRequest) decoded.getRequest());
	    if (getDeviceRegistrationEventsProducer().getLifecycleStatus() == LifecycleStatus.Started) {
		// Build and forward device registration payload message.
		getDeviceRegistrationEventsProducer().send(decoded.getDeviceToken(),
			DeviceModelMarshaler.buildDeviceRegistrationPayloadMessage(payload));
		logPipelineEvent(sourceUnique, payload.getDeviceToken(), getMicroservice().getIdentifier(),
			"Decoded registration event and forwarded to Kafka registration events topic.", null,
			EventPipelineLogLevel.Debug);
	    } else {
		getLogger().warn("Producer not started. Unable to add device registration event to topic.");
		logPipelineEvent(sourceUnique, payload.getDeviceToken(), getMicroservice().getIdentifier(),
			"Kafka producer was not started. Unable to forward event.", null, EventPipelineLogLevel.Debug);
	    }
	} else {
	    getLogger().warn(String.format("Request parsed from payload was not handled: %s",
		    decoded.getRequest().getClass().getName()));
	}
    }

    /*
     * @see com.sitewhere.sources.spi.IEventSourcesManager#handleFailedDecode(java.
     * lang.String, byte[], java.util.Map, java.lang.Throwable)
     */
    @Override
    public void handleFailedDecode(String sourceId, byte[] encoded, Map<String, Object> metadata, Throwable t)
	    throws SiteWhereException {
	getLogger().warn("Event could not be decoded. Adding to failed decode topic.", t);
	if (getFailedDecodeEventsProducer().getLifecycleStatus() == LifecycleStatus.Started) {
	    getFailedDecodeEventsProducer().send(sourceId, encoded);
	} else if (getLogger().isWarnEnabled()) {
	    getLogger().warn("Producer not started. Unable to add event to topic.");
	}
    }

    /*
     * @see com.sitewhere.sources.spi.IEventSourcesManager#getEventSources()
     */
    @Override
    public List<IInboundEventSource<?>> getEventSources() {
	return eventSources;
    }

    /*
     * @see
     * com.sitewhere.sources.spi.IEventSourcesManager#getDecodedEventsProducer()
     */
    @Override
    public DecodedEventsProducer getDecodedEventsProducer() {
	return decodedEventsProducer;
    }

    /*
     * @see
     * com.sitewhere.sources.spi.IEventSourcesManager#getFailedDecodeEventsProducer(
     * )
     */
    @Override
    public FailedDecodeEventsProducer getFailedDecodeEventsProducer() {
	return failedDecodeEventsProducer;
    }

    /*
     * @see com.sitewhere.sources.spi.IEventSourcesManager#
     * getDeviceRegistrationEventsProducer()
     */
    @Override
    public DeviceRegistrationEventsProducer getDeviceRegistrationEventsProducer() {
	return deviceRegistrationEventsProducer;
    }

    protected EventSourcesTenantConfiguration getConfiguration() {
	return configuration;
    }
}