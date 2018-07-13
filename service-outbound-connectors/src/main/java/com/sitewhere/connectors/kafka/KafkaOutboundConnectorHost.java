/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.kafka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.connectors.spi.IOutboundConnector;
import com.sitewhere.grpc.kafka.model.KafkaModel.GEnrichedEventPayload;
import com.sitewhere.grpc.model.converter.KafkaModelConverter;
import com.sitewhere.grpc.model.marshaler.KafkaModelMarshaler;
import com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.rest.model.microservice.kafka.payload.EnrichedEventPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.kafka.payload.IEnrichedEventPayload;
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

    /** Max number of Kafka commits that may be queued */
    private static final int MAX_COMMIT_QUEUE_SIZE = 100;

    /** Get wrapped outbound connector implementation */
    private IOutboundConnector outboundConnector;

    /** Last partition offsets */
    private ConcurrentHashMap<TopicPartition, Long> partitionOffsets = new ConcurrentHashMap<>();

    /** Queue of commits to be processed */
    private BlockingQueue<TopicPartitionWithOffset> commitQueue = new ArrayBlockingQueue<>(MAX_COMMIT_QUEUE_SIZE);

    /** Commit processor executor */
    private ExecutorService commitProcessor;

    /** Batch processors executor */
    private ExecutorService batchProcessors;

    public KafkaOutboundConnectorHost(IOutboundConnector outboundConnector) {
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
	batchProcessors = Executors.newFixedThreadPool(getOutboundConnector().getNumProcessingThreads(),
		new EventPayloadProcessorThreadFactory());
	commitProcessor = Executors.newSingleThreadExecutor();
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
	if (batchProcessors != null) {
	    batchProcessors.shutdown();
	    try {
		batchProcessors.awaitTermination(10, TimeUnit.SECONDS);
	    } catch (InterruptedException e) {
		getLogger().error("Batch processors for connector did not terminate within timout period.");
	    }
	}
	if (commitProcessor != null) {
	    commitProcessor.shutdown();
	    try {
		commitProcessor.awaitTermination(10, TimeUnit.SECONDS);
	    } catch (InterruptedException e) {
		getLogger().error("Commit processor for connector did not terminate within timout period.");
	    }
	}
	stopNestedComponent(getOutboundConnector(), monitor);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#process(org.
     * apache.kafka.common.TopicPartition, java.util.List)
     */
    @Override
    public void process(TopicPartition topicPartition, List<ConsumerRecord<String, byte[]>> records) {
	if (records.size() > 0) {
	    batchProcessors.execute(new TopicBatchProcessor(topicPartition, records));
	}
    }

    /**
     * Commit partition offset if it's greater than previous commits.
     * 
     * @param topicPartition
     * @param offset
     */
    protected synchronized void commit(TopicPartition topicPartition, long newOffset) {
	Long existing = getPartitionOffsets().get(topicPartition);
	if ((existing != null) && (newOffset < existing)) {
	    return;
	}

	// Store new offset.
	getPartitionOffsets().put(topicPartition, newOffset);

	TopicPartitionWithOffset entry = new TopicPartitionWithOffset();
	entry.setTopicPartition(topicPartition);
	entry.setOffset(newOffset);
	getCommitQueue().offer(entry);
    }

    protected IOutboundConnector getOutboundConnector() {
	return outboundConnector;
    }

    protected ConcurrentHashMap<TopicPartition, Long> getPartitionOffsets() {
	return partitionOffsets;
    }

    protected BlockingQueue<TopicPartitionWithOffset> getCommitQueue() {
	return commitQueue;
    }

    /**
     * Process Kafka commits in a single thread.
     * 
     * @author Derek
     */
    protected class CommitProcessor implements Runnable {

	@Override
	public void run() {
	    while (true) {
		try {
		    TopicPartitionWithOffset entry = getCommitQueue().take();

		    // Prepare new offset information.
		    OffsetAndMetadata offset = new OffsetAndMetadata(entry.getOffset());
		    Map<TopicPartition, OffsetAndMetadata> update = new HashMap<>();
		    update.put(entry.getTopicPartition(), offset);

		    // Send new offset information.
		    getConsumer().commitAsync(update, new OffsetCommitCallback() {
			public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception e) {
			    if (e != null) {
				getLogger().error("Commit failed for offsets " + offsets, e);
			    }
			}
		    });
		} catch (Throwable t) {
		    getLogger().error("Unhandled exception in commit processor.", t);
		}
	    }
	}
    }

    /**
     * Processor that unmarshals an enriched event and forwards it to outbound
     * connector implementation.
     * 
     * @author Derek
     */
    protected class TopicBatchProcessor extends SystemUserRunnable {

	/** Partition */
	private TopicPartition topicPartition;

	/** Records to process */
	private List<ConsumerRecord<String, byte[]>> records;

	public TopicBatchProcessor(TopicPartition topicPartition, List<ConsumerRecord<String, byte[]>> records) {
	    super(getTenantEngine().getMicroservice(), getTenantEngine().getTenant());
	    this.topicPartition = topicPartition;
	    this.records = records;
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#runAsSystemUser()
	 */
	@Override
	public void runAsSystemUser() throws SiteWhereException {
	    List<IEnrichedEventPayload> decoded = new ArrayList<>();
	    long offset = 0;
	    for (ConsumerRecord<String, byte[]> record : getRecords()) {
		try {
		    GEnrichedEventPayload grpc = KafkaModelMarshaler.parseEnrichedEventPayloadMessage(record.value());
		    EnrichedEventPayload payload = KafkaModelConverter.asApiEnrichedEventPayload(grpc);
		    if (getLogger().isDebugEnabled()) {
			getLogger().debug("Received enriched event payload:\n\n"
				+ MarshalUtils.marshalJsonAsPrettyString(payload));
		    }
		    decoded.add(payload);
		    offset = record.offset();
		} catch (SiteWhereException e) {
		    getLogger().error("Unable to parse outbound connector event payload.", e);
		} catch (Throwable e) {
		    getLogger().error("Unhandled exception parsing connector event payload.", e);
		}
	    }
	    try {
		getOutboundConnector().processEventBatch(decoded);
		commit(getTopicPartition(), offset);
	    } catch (SiteWhereException e) {
		getOutboundConnector().handleFailedBatch(decoded, e);
		getLogger().error("Unable to process outbound connector batch.", e);
	    } catch (Throwable e) {
		getOutboundConnector().handleFailedBatch(decoded, e);
		getLogger().error("Unhandled exception processing connector batch.", e);
	    }
	}

	public TopicPartition getTopicPartition() {
	    return topicPartition;
	}

	public void setTopicPartition(TopicPartition topicPartition) {
	    this.topicPartition = topicPartition;
	}

	public List<ConsumerRecord<String, byte[]>> getRecords() {
	    return records;
	}

	public void setRecords(List<ConsumerRecord<String, byte[]>> records) {
	    this.records = records;
	}
    }

    /**
     * Wrapper for pointer to current offset in a partition.
     * 
     * @author Derek
     */
    private class TopicPartitionWithOffset {

	/** Topic partition */
	private TopicPartition topicPartition;

	/** Topic offset */
	private long offset;

	public TopicPartition getTopicPartition() {
	    return topicPartition;
	}

	public void setTopicPartition(TopicPartition topicPartition) {
	    this.topicPartition = topicPartition;
	}

	public long getOffset() {
	    return offset;
	}

	public void setOffset(long offset) {
	    this.offset = offset;
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