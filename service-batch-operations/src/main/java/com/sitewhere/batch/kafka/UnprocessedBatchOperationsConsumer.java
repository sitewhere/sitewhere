/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;

import com.sitewhere.batch.spi.kafka.IUnprocessedBatchOperationsConsumer;
import com.sitewhere.common.MarshalUtils;
import com.sitewhere.grpc.client.batch.BatchModelConverter;
import com.sitewhere.grpc.client.batch.BatchModelMarshaler;
import com.sitewhere.grpc.model.BatchModel.GBatchOperation;
import com.sitewhere.microservice.kafka.DirectKafkaConsumer;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.rest.model.batch.BatchOperation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Listens on Kafka topic for unprocessed batch operations.
 */
public class UnprocessedBatchOperationsConsumer extends DirectKafkaConsumer
	implements IUnprocessedBatchOperationsConsumer {

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Suffix for group id */
    private static String GROUP_ID_SUFFIX = "unprocessed-batch-operation-consumers";

    /** Number of threads initializing batch operations */
    private static final int CONCURRENT_BATCH_OPERATION_INIT_THREADS = 10;

    /** Executor */
    private ExecutorService executor;

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
		.getUnprocessedBatchOperationsTopic(getTenantEngine().getTenant()));
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
	executor = Executors.newFixedThreadPool(CONCURRENT_BATCH_OPERATION_INIT_THREADS,
		new BatchOperationInitializerThreadFactory());
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
	executor.execute(new BatchOperationInitializationProcessor(getTenantEngine(), message));
    }

    /**
     * Processor that initializes a batch operation by creating the batch elements
     * which will be used to track operation progress.
     */
    protected class BatchOperationInitializationProcessor extends SystemUserRunnable {

	/** Encoded payload */
	private byte[] encoded;

	public BatchOperationInitializationProcessor(IMicroserviceTenantEngine tenantEngine, byte[] encoded) {
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
		GBatchOperation grpc = BatchModelMarshaler.parseBatchOperationPayloadMessage(encoded);
		if (getLogger().isDebugEnabled()) {
		    BatchOperation payload = BatchModelConverter.asApiBatchOperation(grpc);
		    getLogger().debug(
			    "Received batch operation payload:\n\n" + MarshalUtils.marshalJsonAsPrettyString(payload));
		}
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to parse batch operation payload.", e);
	    }
	}
    }

    /** Used for naming batch operations initializer threads */
    private class BatchOperationInitializerThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Batch Operation Init " + counter.incrementAndGet());
	}
    }
}