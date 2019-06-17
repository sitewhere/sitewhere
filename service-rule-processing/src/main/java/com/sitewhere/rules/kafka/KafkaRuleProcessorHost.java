/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rules.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.grpc.client.event.EventModelConverter;
import com.sitewhere.grpc.client.event.EventModelMarshaler;
import com.sitewhere.grpc.model.DeviceEventModel.GEnrichedEventPayload;
import com.sitewhere.microservice.kafka.DirectKafkaConsumer;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.rest.model.device.event.kafka.EnrichedEventPayload;
import com.sitewhere.rules.spi.IRuleProcessor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Kafka host container that reads from the enriched events topic and forwards
 * the messages to a wrapped rule processor.
 * 
 * @author Derek
 */
public class KafkaRuleProcessorHost extends DirectKafkaConsumer {

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Get wrapped rule processor implementation */
    private IRuleProcessor ruleProcessor;

    /** Executor */
    private ExecutorService executor;

    public KafkaRuleProcessorHost(IRuleProcessor ruleProcessor) {
	this.ruleProcessor = ruleProcessor;
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
	return getMicroservice().getKafkaTopicNaming().getTenantPrefix(getTenantEngine().getTenant())
		+ "rule-processor." + getRuleProcessor().getProcessorId();
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#
     * getSourceTopicNames()
     */
    @Override
    public List<String> getSourceTopicNames() throws SiteWhereException {
	List<String> topics = new ArrayList<String>();
	topics.add(getMicroservice().getKafkaTopicNaming().getOutboundEventsTopic(getTenantEngine().getTenant()));
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
	initializeNestedComponent(getRuleProcessor(), monitor, true);
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
	startNestedComponent(getRuleProcessor(), monitor, true);
	executor = Executors.newFixedThreadPool(getRuleProcessor().getNumProcessingThreads(),
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
		getLogger().error("Rule processor host did not terminate within timout period.");
	    }
	}
	stopNestedComponent(getRuleProcessor(), monitor);
    }

    /*
     * @see
     * com.sitewhere.microservice.kafka.DirectKafkaConsumer#attemptToProcess(org.
     * apache.kafka.common.TopicPartition, java.util.List)
     */
    @Override
    public void attemptToProcess(TopicPartition topicPartition, List<ConsumerRecord<String, byte[]>> records)
	    throws SiteWhereException {
	for (ConsumerRecord<String, byte[]> record : records) {
	    received(record.key(), record.value());
	}
    }

    public void received(String key, byte[] message) throws SiteWhereException {
	executor.execute(new EventPayloadProcessor(message));
    }

    public IRuleProcessor getRuleProcessor() {
	return ruleProcessor;
    }

    public void setRuleProcessor(IRuleProcessor ruleProcessor) {
	this.ruleProcessor = ruleProcessor;
    }

    /**
     * Processor that unmarshals an enriched event and forwards it to a rule
     * processor implementation.
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
		GEnrichedEventPayload grpc = EventModelMarshaler.parseEnrichedEventPayloadMessage(encoded);
		EnrichedEventPayload payload = EventModelConverter.asApiEnrichedEventPayload(grpc);
		if (getLogger().isDebugEnabled()) {
		    getLogger().debug(
			    "Received enriched event payload:\n\n" + MarshalUtils.marshalJsonAsPrettyString(payload));
		}
		routePayload(payload);
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to process rule processor event payload.", e);
	    } catch (Throwable e) {
		getLogger().error("Unhandled exception processing rule processor event payload.", e);
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
		getRuleProcessor().onAlert(context, (IDeviceAlert) event);
		break;
	    }
	    case CommandInvocation: {
		getRuleProcessor().onCommandInvocation(context, (IDeviceCommandInvocation) event);
		break;
	    }
	    case CommandResponse: {
		getRuleProcessor().onCommandResponse(context, (IDeviceCommandResponse) event);
		break;
	    }
	    case Location: {
		getRuleProcessor().onLocation(context, (IDeviceLocation) event);
		break;
	    }
	    case Measurement: {
		getRuleProcessor().onMeasurement(context, (IDeviceMeasurement) event);
		break;
	    }
	    case StateChange: {
		getRuleProcessor().onStateChange(context, (IDeviceStateChange) event);
		break;
	    }
	    default: {
		throw new SiteWhereException("Unknown event type. " + event.getEventType().name());
	    }
	    }
	}
    }

    /** Used for naming event payload processing threads */
    private class EventPayloadProcessorThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r,
		    "Rule Processor '" + getRuleProcessor().getProcessorId() + "' " + counter.incrementAndGet());
	}
    }
}