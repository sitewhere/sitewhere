/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.destinations.kafka;

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
import com.sitewhere.destinations.microservice.CommandDestinationsTenantEngine;
import com.sitewhere.destinations.spi.kafka.IEnrichedCommandInvocationsConsumer;
import com.sitewhere.destinations.spi.microservice.ICommandDestinationsTenantEngine;
import com.sitewhere.grpc.kafka.model.KafkaModel.GEnrichedEventPayload;
import com.sitewhere.grpc.model.converter.KafkaModelConverter;
import com.sitewhere.grpc.model.marshaling.KafkaModelMarshaler;
import com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.rest.model.microservice.kafka.payload.EnrichedEventPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Consumes command invocations from a well-known Kafka topic and makes them
 * available to the command destinations manager.
 * 
 * @author Derek
 */
public class EnrichedCommandInvocationsConsumer extends MicroserviceKafkaConsumer
	implements IEnrichedCommandInvocationsConsumer {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Suffix for group id */
    private static String GROUP_ID_SUFFIX = "enriched-command-invocation-consumers";

    /** Number of threads processing command invocations */
    private static final int CONCURRENT_EVENT_PROCESSING_THREADS = 5;

    /** Executor */
    private ExecutorService executor;

    public EnrichedCommandInvocationsConsumer(CommandDestinationsTenantEngine tenantEngine) {
	super(tenantEngine.getMicroservice(), tenantEngine);
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
		.getInboundEnrichedCommandInvocationsTopic(getTenantEngine().getTenant()));
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
		new CommandInvocationProcessorThreadFactory());
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
	executor.execute(new CommandInvocationProcessor(getTenantEngine(), message));
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    /**
     * Processor that unmarshals a decoded event for a command invocation and hands
     * it off for processing.
     * 
     * @author Derek
     */
    protected class CommandInvocationProcessor extends SystemUserRunnable {

	/** Encoded payload */
	private byte[] encoded;

	public CommandInvocationProcessor(IMicroserviceTenantEngine tenantEngine, byte[] encoded) {
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
		GEnrichedEventPayload grpc = KafkaModelMarshaler.parseEnrichedEventPayloadMessage(encoded);
		EnrichedEventPayload eventPayload = KafkaModelConverter.asApiEnrichedEventPayload(grpc);
		if (getLogger().isDebugEnabled()) {
		    getLogger().debug(
			    "Received command invocation:\n\n" + MarshalUtils.marshalJsonAsPrettyString(eventPayload));
		}

		// Pass decoded payload to command destinations manager.
		((ICommandDestinationsTenantEngine) getTenantEngine()).getCommandDestinationsManager()
			.processCommandInvocation(eventPayload);
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to parse unregistered device event payload.", e);
	    } catch (Throwable e) {
		getLogger().error("Unhandled exception parsing unregistered device event payload.", e);
	    }
	}
    }

    /** Used for naming command invocation processing threads */
    private class CommandInvocationProcessorThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Command Invocations " + counter.incrementAndGet());
	}
    }
}