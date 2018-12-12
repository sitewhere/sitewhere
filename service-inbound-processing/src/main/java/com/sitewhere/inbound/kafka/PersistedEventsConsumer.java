/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.grpc.client.event.EventModelConverter;
import com.sitewhere.grpc.client.event.EventModelMarshaler;
import com.sitewhere.grpc.model.DeviceEventModel.GPersistedEventPayload;
import com.sitewhere.inbound.processing.OutboundPayloadEnrichmentLogic;
import com.sitewhere.inbound.spi.kafka.IPersistedEventsConsumer;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine;
import com.sitewhere.microservice.kafka.DirectKafkaConsumer;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.rest.model.device.event.kafka.PersistedEventPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Listens on Kafka topic for events that have been persisted via the event
 * management APIs.
 * 
 * @author Derek
 */
public class PersistedEventsConsumer extends DirectKafkaConsumer implements IPersistedEventsConsumer {

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Suffix for group id */
    private static String GROUP_ID_SUFFIX = "persisted-event-consumers";

    /** Number of threads processing inbound events */
    private static final int CONCURRENT_EVENT_PROCESSING_THREADS = 10;

    /** Executor */
    private ExecutorService executor;

    /** Logic for enriching outbound event payload */
    private OutboundPayloadEnrichmentLogic outboundPayloadEnrichmentLogic;

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
	return getMicroservice().getKafkaTopicNaming().getTenantPrefix(getTenantEngine().getTenant()) + GROUP_ID_SUFFIX;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#
     * getSourceTopicNames()
     */
    @Override
    public List<String> getSourceTopicNames() throws SiteWhereException {
	List<String> topics = new ArrayList<String>();
	topics.add(
		getMicroservice().getKafkaTopicNaming().getInboundPersistedEventsTopic(getTenantEngine().getTenant()));
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
	this.outboundPayloadEnrichmentLogic = new OutboundPayloadEnrichmentLogic(
		(IInboundProcessingTenantEngine) getTenantEngine());
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
	executor = Executors.newFixedThreadPool(CONCURRENT_EVENT_PROCESSING_THREADS,
		new PersistedEventProcessingThreadFactory());
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
	}
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
	executor.execute(new PersistedEventPayloadProcessor(getTenantEngine(), message));
    }

    public OutboundPayloadEnrichmentLogic getOutboundPayloadEnrichmentLogic() {
	return outboundPayloadEnrichmentLogic;
    }

    public void setOutboundPayloadEnrichmentLogic(OutboundPayloadEnrichmentLogic outboundPayloadEnrichmentLogic) {
	this.outboundPayloadEnrichmentLogic = outboundPayloadEnrichmentLogic;
    }

    /**
     * Processor that unmarshals a persisted event and processes it.
     * 
     * @author Derek
     */
    protected class PersistedEventPayloadProcessor extends SystemUserRunnable {

	/** Encoded payload */
	private byte[] encoded;

	public PersistedEventPayloadProcessor(IMicroserviceTenantEngine tenantEngine, byte[] encoded) {
	    super(tenantEngine.getMicroservice(), tenantEngine.getTenant());
	    this.encoded = encoded;
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#
	 * runAsSystemUser()
	 */
	@Override
	public void runAsSystemUser() throws SiteWhereException {
	    try {
		GPersistedEventPayload grpc = EventModelMarshaler.parsePersistedEventPayloadMessage(encoded);
		if (getLogger().isDebugEnabled()) {
		    PersistedEventPayload payload = EventModelConverter.asApiPersisedEventPayload(grpc);
		    getLogger().debug(
			    "Received persisted event payload:\n\n" + MarshalUtils.marshalJsonAsPrettyString(payload));
		}
		getOutboundPayloadEnrichmentLogic().process(grpc);
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to parse persisted event payload.", e);
	    }
	}
    }

    /** Used for naming persisted event processing threads */
    private class PersistedEventProcessingThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Persisted Event Processing " + counter.incrementAndGet());
	}
    }
}