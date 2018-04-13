/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.connectors.spi.IOutboundConnector;
import com.sitewhere.grpc.kafka.model.KafkaModel.GEnrichedEventPayload;
import com.sitewhere.grpc.model.converter.KafkaModelConverter;
import com.sitewhere.grpc.model.marshaler.KafkaModelMarshaler;
import com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.rest.model.microservice.kafka.payload.EnrichedEventPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Kafka host container that reads from the enriched events topic and forwards
 * the messages to a wrapped outbound connector.
 * 
 * @author Derek
 */
public class KafkaOutboundConnectorHost extends MicroserviceKafkaConsumer {

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Get wrapped outbound connector implementation */
    private IOutboundConnector outboundConnector;

    /** Executor */
    private ExecutorService executor;

    public KafkaOutboundConnectorHost(IMicroservice microservice, IMicroserviceTenantEngine tenantEngine,
	    IOutboundConnector outboundConnector) {
	super(microservice, tenantEngine);
	this.outboundConnector = outboundConnector;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#
     * getConsumerId()
     */
    @Override
    public String getConsumerId() throws SiteWhereException {
	return CONSUMER_ID;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#
     * getConsumerGroupId()
     */
    @Override
    public String getConsumerGroupId() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming().getTenantPrefix(getTenantEngine().getTenant()) + "connector."
		+ getOutboundConnector().getConnectorId();
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#
     * getSourceTopicNames()
     */
    @Override
    public List<String> getSourceTopicNames() throws SiteWhereException {
	List<String> topics = new ArrayList<String>();
	topics.add(
		getMicroservice().getKafkaTopicNaming().getInboundEnrichedEventsTopic(getTenantEngine().getTenant()));
	return topics;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);
	initializeNestedComponent(getOutboundConnector(), monitor, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);
	startNestedComponent(getOutboundConnector(), monitor, true);
	executor = Executors.newFixedThreadPool(getOutboundConnector().getNumProcessingThreads(),
		new EventPayloadProcessorThreadFactory());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer#stop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);
	if (executor != null) {
	    executor.shutdown();
	    try {
		executor.awaitTermination(10, TimeUnit.SECONDS);
	    } catch (InterruptedException e) {
		getLogger().error("Outbound connector host did not terminate within timout period.");
	    }
	}
	stopNestedComponent(getOutboundConnector(), monitor);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#processBatch(
     * java.util.List)
     */
    @Override
    public void processBatch(List<ConsumerRecord<String, byte[]>> records) throws SiteWhereException {
	for (ConsumerRecord<String, byte[]> record : records) {
	    received(record.key(), record.value());
	}
    }

    public void received(String key, byte[] message) throws SiteWhereException {
	executor.execute(new EventPayloadProcessor(message));
    }

    public IOutboundConnector getOutboundConnector() {
	return outboundConnector;
    }

    public void setOutboundConnector(IOutboundConnector outboundConnector) {
	this.outboundConnector = outboundConnector;
    }

    /**
     * Processor that unmarshals an enriched event and forwards it to outbound
     * connector implementation.
     * 
     * @author Derek
     */
    protected class EventPayloadProcessor extends SystemUserRunnable {

	/** Encoded event payload */
	private byte[] encoded;

	public EventPayloadProcessor(byte[] encoded) {
	    super(getTenantEngine().getMicroservice(), getTenantEngine().getTenant());
	    this.encoded = encoded;
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#runAsSystemUser()
	 */
	@Override
	public void runAsSystemUser() throws SiteWhereException {
	    try {
		GEnrichedEventPayload grpc = KafkaModelMarshaler.parseEnrichedEventPayloadMessage(encoded);
		EnrichedEventPayload payload = KafkaModelConverter.asApiEnrichedEventPayload(grpc);
		if (getLogger().isDebugEnabled()) {
		    getLogger().debug(
			    "Received enriched event payload:\n\n" + MarshalUtils.marshalJsonAsPrettyString(payload));
		}
		routePayload(payload);
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to process outbound connector event payload.", e);
	    } catch (Throwable e) {
		getLogger().error("Unhandled exception processing connector event payload.", e);
	    }
	}

	/**
	 * Route payload to correct processor method.
	 * 
	 * @param payload
	 * @throws SiteWhereException
	 */
	protected void routePayload(EnrichedEventPayload payload) throws SiteWhereException {
	    IDeviceEventContext context = payload.getEventContext();
	    IDeviceEvent event = payload.getEvent();
	    switch (event.getEventType()) {
	    case Alert: {
		getOutboundConnector().onAlert(context, (IDeviceAlert) event);
		break;
	    }
	    case CommandInvocation: {
		getOutboundConnector().onCommandInvocation(context, (IDeviceCommandInvocation) event);
		break;
	    }
	    case CommandResponse: {
		getOutboundConnector().onCommandResponse(context, (IDeviceCommandResponse) event);
		break;
	    }
	    case Location: {
		getOutboundConnector().onLocation(context, (IDeviceLocation) event);
		break;
	    }
	    case Measurements: {
		getOutboundConnector().onMeasurements(context, (IDeviceMeasurements) event);
		break;
	    }
	    case StateChange: {
		getOutboundConnector().onStateChange(context, (IDeviceStateChange) event);
		break;
	    }
	    default: {
		throw new SiteWhereException("Unknown event type. " + event.getEventType().name());
	    }
	    }
	}
    }

    /** Used for naming outbound event processing threads */
    private class EventPayloadProcessorThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Outbound Connector '" + getOutboundConnector().getConnectorId() + "' "
		    + counter.incrementAndGet());
	}
    }
}