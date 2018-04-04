/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.state;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.sitewhere.grpc.kafka.model.KafkaModel.GStateUpdate;
import com.sitewhere.grpc.model.converter.KafkaModelConverter;
import com.sitewhere.grpc.model.marshaler.KafkaModelMarshaler;
import com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.state.IMicroserviceStateUpdatesKafkaConsumer;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Base class for Kafka consumers that process state updates for microservices
 * and their managed tenant engines.
 * 
 * @author Derek
 */
public abstract class MicroserviceStateUpdatesKafkaConsumer extends MicroserviceKafkaConsumer
	implements IMicroserviceStateUpdatesKafkaConsumer {

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Unique group id as each consumer should see all messages */
    private static String GROUP_ID_SUFFIX = UUID.randomUUID().toString();

    /** Queue for inbound messages */
    private BlockingDeque<byte[]> queuedStateUpdates = new LinkedBlockingDeque<>();

    /** Provides thread pool */
    private ExecutorService executor;

    public MicroserviceStateUpdatesKafkaConsumer(IMicroservice microservice) {
	super(microservice, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);
	executor = Executors.newSingleThreadExecutor(new MicroserviceStateUpdateThreadFactory());
	executor.execute(new StateUpdateProcessor());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (executor != null) {
	    executor.shutdownNow();
	}
	super.stop(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.kafka.IMicroserviceKafkaConsumer#
     * getConsumerId()
     */
    @Override
    public String getConsumerId() throws SiteWhereException {
	return CONSUMER_ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.kafka.IMicroserviceKafkaConsumer#
     * getConsumerGroupId()
     */
    @Override
    public String getConsumerGroupId() throws SiteWhereException {
	return getMicroservice().getKafkaTopicNaming().getInstancePrefix() + GROUP_ID_SUFFIX;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#
     * getSourceTopicNames()
     */
    @Override
    public List<String> getSourceTopicNames() throws SiteWhereException {
	return Collections.singletonList(getMicroservice().getKafkaTopicNaming().getMicroserviceStateUpdatesTopic());
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
	try {
	    getQueuedStateUpdates().put(message);
	} catch (InterruptedException e) {
	    getLogger().warn("Interrupted while waiting to process state update.", e);
	}
    }

    /**
     * Handles state updates in a separate thread.
     * 
     * @author Derek
     */
    private class StateUpdateProcessor implements Runnable {

	/*
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
	    while (true) {
		try {
		    byte[] message = getQueuedStateUpdates().take();
		    GStateUpdate update = KafkaModelMarshaler.parseStateUpdateMessage(message);

		    switch (update.getStateCase()) {
		    case MICROSERVICESTATE: {
			onMicroserviceStateUpdate(
				KafkaModelConverter.asApiMicroserviceState(update.getMicroserviceState()));
			break;
		    }
		    case TENANTENGINESTATE: {
			onTenantEngineStateUpdate(
				KafkaModelConverter.asApiTenantEngineState(update.getTenantEngineState()));
			break;
		    }
		    case STATE_NOT_SET: {
			getLogger().warn("Invalid state message received: " + update.getStateCase().name());
		    }
		    }
		} catch (InterruptedException e) {
		    getLogger().warn("State update thread shutting down.");
		    return;
		} catch (SiteWhereException e) {
		    getLogger().error("Error in microservice state processing.", e);
		} catch (Throwable t) {
		    getLogger().error("Unhandled exception in microservice state processing.", t);
		}
	    }
	}
    }

    public BlockingDeque<byte[]> getQueuedStateUpdates() {
	return queuedStateUpdates;
    }

    public void setQueuedStateUpdates(BlockingDeque<byte[]> queuedStateUpdates) {
	this.queuedStateUpdates = queuedStateUpdates;
    }

    /** Used for naming microservice state update thread */
    private class MicroserviceStateUpdateThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Microservice State Update " + counter.incrementAndGet());
	}
    }
}