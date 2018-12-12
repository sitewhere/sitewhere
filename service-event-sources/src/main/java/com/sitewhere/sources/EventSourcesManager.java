/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources;

import java.util.List;
import java.util.Map;

import com.sitewhere.grpc.client.event.EventModelMarshaler;
import com.sitewhere.rest.model.device.event.kafka.DeviceRegistrationPayload;
import com.sitewhere.rest.model.device.event.kafka.InboundEventPayload;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.sources.kafka.DecodedEventsProducer;
import com.sitewhere.sources.kafka.DeviceRegistrationEventsProducer;
import com.sitewhere.sources.kafka.FailedDecodeEventsProducer;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IEventSourcesManager;
import com.sitewhere.sources.spi.IInboundEventSource;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Manages lifecycle of the list of event sources configured for a tenant.
 * 
 * @author Derek
 */
public class EventSourcesManager extends TenantEngineLifecycleComponent implements IEventSourcesManager {

    /** List of event sources */
    private List<IInboundEventSource<?>> eventSources;

    /** Kafka producer for decoded events from event sources */
    private DecodedEventsProducer decodedEventsProducer;

    /** Kafka producer for events that could not be decoded */
    private FailedDecodeEventsProducer failedDecodeEventsProducer;

    /** Kafka producer for device registation events from event sources */
    private DeviceRegistrationEventsProducer deviceRegistrationEventsProducer;

    /*
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
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
	if (decoded.getRequest() instanceof IDeviceEventCreateRequest) {
	    if (getDecodedEventsProducer().getLifecycleStatus() == LifecycleStatus.Started) {
		// Build and forward inbound event payload message.
		InboundEventPayload payload = new InboundEventPayload();
		payload.setSourceId(sourceId);
		payload.setDeviceToken(decoded.getDeviceToken());
		payload.setOriginator(decoded.getOriginator());
		payload.setEventCreateRequest((IDeviceEventCreateRequest) decoded.getRequest());
		getDecodedEventsProducer().send(decoded.getDeviceToken(),
			EventModelMarshaler.buildInboundEventPayloadMessage(payload));
	    } else {
		getLogger().warn("Producer not started. Unable to add decoded event to topic.");
	    }
	} else if (decoded.getRequest() instanceof IDeviceRegistrationRequest) {
	    if (getDeviceRegistrationEventsProducer().getLifecycleStatus() == LifecycleStatus.Started) {
		// Build and forward device registration payload message.
		DeviceRegistrationPayload payload = new DeviceRegistrationPayload();
		payload.setSourceId(sourceId);
		payload.setDeviceToken(decoded.getDeviceToken());
		payload.setOriginator(decoded.getOriginator());
		payload.setDeviceRegistrationRequest((IDeviceRegistrationRequest) decoded.getRequest());
		getDeviceRegistrationEventsProducer().send(decoded.getDeviceToken(),
			EventModelMarshaler.buildDeviceRegistrationPayloadMessage(payload));
	    } else {
		getLogger().warn("Producer not started. Unable to add device registration event to topic.");
	    }
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

    public void setEventSources(List<IInboundEventSource<?>> eventSources) {
	this.eventSources = eventSources;
    }

    /*
     * @see
     * com.sitewhere.sources.spi.IEventSourcesManager#getDecodedEventsProducer()
     */
    @Override
    public DecodedEventsProducer getDecodedEventsProducer() {
	return decodedEventsProducer;
    }

    public void setDecodedEventsProducer(DecodedEventsProducer decodedEventsProducer) {
	this.decodedEventsProducer = decodedEventsProducer;
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

    public void setFailedDecodeEventsProducer(FailedDecodeEventsProducer failedDecodeEventsProducer) {
	this.failedDecodeEventsProducer = failedDecodeEventsProducer;
    }

    /*
     * @see com.sitewhere.sources.spi.IEventSourcesManager#
     * getDeviceRegistrationEventsProducer()
     */
    @Override
    public DeviceRegistrationEventsProducer getDeviceRegistrationEventsProducer() {
	return deviceRegistrationEventsProducer;
    }

    public void setDeviceRegistrationEventsProducer(DeviceRegistrationEventsProducer deviceRegistrationEventsProducer) {
	this.deviceRegistrationEventsProducer = deviceRegistrationEventsProducer;
    }
}