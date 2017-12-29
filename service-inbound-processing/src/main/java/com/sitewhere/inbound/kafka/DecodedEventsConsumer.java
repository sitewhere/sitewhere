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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.grpc.kafka.model.KafkaModel.GInboundEventPayload;
import com.sitewhere.grpc.model.converter.KafkaModelConverter;
import com.sitewhere.grpc.model.marshaling.KafkaModelMarshaler;
import com.sitewhere.inbound.processing.InboundPayloadProcessingLogic;
import com.sitewhere.inbound.spi.kafka.IDecodedEventsConsumer;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingMicroservice;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine;
import com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.rest.model.microservice.kafka.payload.InboundEventPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Listens on Kafka topic for decoded events, making them available for inbound
 * processing.
 * 
 * @author Derek
 */
public class DecodedEventsConsumer extends MicroserviceKafkaConsumer implements IDecodedEventsConsumer {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Suffix for group id */
    private static String GROUP_ID_SUFFIX = "decoded-event-consumers";

    /** Number of threads processing inbound events */
    private static final int CONCURRENT_EVENT_PROCESSING_THREADS = 10;

    /** Executor */
    private ExecutorService executor;

    /** Inbound payload processing logic */
    private InboundPayloadProcessingLogic inboundPayloadProcessingLogic;

    public DecodedEventsConsumer(IInboundProcessingMicroservice microservice,
	    IInboundProcessingTenantEngine tenantEngine) {
	super(microservice, tenantEngine);
	this.inboundPayloadProcessingLogic = new InboundPayloadProcessingLogic(tenantEngine);
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
	return getMicroservice().getKafkaTopicNaming().getTenantPrefix(getTenantEngine().getTenant()) + GROUP_ID_SUFFIX;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#
     * getSourceTopicNames()
     */
    @Override
    public List<String> getSourceTopicNames() throws SiteWhereException {
	List<String> topics = new ArrayList<String>();
	topics.add(getMicroservice().getKafkaTopicNaming()
		.getEventSourceDecodedEventsTopic(getTenantEngine().getTenant()));
	topics.add(
		getMicroservice().getKafkaTopicNaming().getInboundReprocessEventsTopic(getTenantEngine().getTenant()));
	return topics;
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
		new InboundEventProcessingThreadFactory());
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
		getLogger().warn("Executor did not terminate within allotted time.");
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#received(
     * java.lang.String, byte[])
     */
    @Override
    public void received(String key, byte[] message) throws SiteWhereException {
	executor.execute(new InboundEventPayloadProcessor(getTenantEngine(), message));
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public InboundPayloadProcessingLogic getInboundPayloadProcessingLogic() {
	return inboundPayloadProcessingLogic;
    }

    public void setInboundPayloadProcessingLogic(InboundPayloadProcessingLogic inboundPayloadProcessingLogic) {
	this.inboundPayloadProcessingLogic = inboundPayloadProcessingLogic;
    }

    /**
     * Processor that unmarshals a decoded event and forwards it for registration
     * verification.
     * 
     * @author Derek
     */
    protected class InboundEventPayloadProcessor extends SystemUserRunnable {

	/** Encoded payload */
	private byte[] encoded;

	public InboundEventPayloadProcessor(IMicroserviceTenantEngine tenantEngine, byte[] encoded) {
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
		GInboundEventPayload grpc = KafkaModelMarshaler.parseInboundEventPayloadMessage(encoded);
		if (getLogger().isDebugEnabled()) {
		    InboundEventPayload payload = KafkaModelConverter.asApiInboundEventPayload(grpc);
		    getLogger().debug(
			    "Received decoded event payload:\n\n" + MarshalUtils.marshalJsonAsPrettyString(payload));
		}
		getInboundPayloadProcessingLogic().process(grpc);
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to parse inbound event payload.", e);
	    }
	}
    }

    /** Used for naming inbound event processing threads */
    private class InboundEventProcessingThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Inbound Event Processing " + counter.incrementAndGet());
	}
    }
}