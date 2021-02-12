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
package com.sitewhere.connectors.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;

import com.sitewhere.connectors.spi.IOutboundConnector;
import com.sitewhere.grpc.event.EventModelConverter;
import com.sitewhere.grpc.event.EventModelMarshaler;
import com.sitewhere.grpc.model.DeviceEventModel.GProcessedEventPayload;
import com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.rest.model.device.event.kafka.ProcessedEventPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.kafka.IProcessedEventPayload;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.microservice.lifecycle.LifecycleStatus;

/**
 * Kafka host container that reads from the enriched events topic and forwards
 * the messages to a wrapped outbound connector.
 */
public class KafkaOutboundConnectorHost extends MicroserviceKafkaConsumer {

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Get wrapped outbound connector implementation */
    private IOutboundConnector outboundConnector;

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
	return getMicroservice().getKafkaTopicNaming().getTenantPrefix(getTenantEngine().getTenantResource())
		+ "connector." + getOutboundConnector().getConnectorId();
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#
     * getSourceTopicNames()
     */
    @Override
    public List<String> getSourceTopicNames() throws SiteWhereException {
	List<String> topics = new ArrayList<String>();
	topics.add(
		getMicroservice().getKafkaTopicNaming().getOutboundEventsTopic(getTenantEngine().getTenantResource()));
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

	// Initialize the wrapped connector component.
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
	// Start the wrapped connector component.
	startNestedComponent(getOutboundConnector(), monitor, true);

	// Only start the consumer if outbound connector started successfully.
	super.start(monitor);

	int numThreads = getOutboundConnector().getNumProcessingThreads();
	getLogger().info(String.format("Connector host starting connector with pool of %d %s.", numThreads,
		numThreads == 1 ? "thread" : "threads"));
	this.batchProcessors = Executors.newFixedThreadPool(getOutboundConnector().getNumProcessingThreads(),
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
	if (getBatchProcessors() != null) {
	    getBatchProcessors().shutdown();
	    try {
		getBatchProcessors().awaitTermination(10, TimeUnit.SECONDS);
	    } catch (InterruptedException e) {
		getLogger().error("Batch processors for connector did not terminate within timout period.");
	    }
	}

	// Stop the wrapped connector component.
	stopNestedComponent(getOutboundConnector(), monitor);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#process(org.
     * apache.kafka.common.TopicPartition, java.util.List)
     */
    @Override
    public void process(TopicPartition topicPartition, List<ConsumerRecord<String, byte[]>> records) {
	if (getOutboundConnector().getLifecycleStatus() == LifecycleStatus.Started) {
	    if (records.size() > 0) {
		getBatchProcessors().execute(new TopicBatchProcessor(this, topicPartition, records));

		// Send new offset information.
		getConsumer().commitAsync(new OffsetCommitCallback() {
		    public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception e) {
			if (e != null) {
			    getLogger().error("Commit failed for offsets " + offsets, e);
			}
		    }
		});
	    }
	} else {
	    getLogger().warn("Skipping record batch due to outbound connector not in started state.");
	}
    }

    protected IOutboundConnector getOutboundConnector() {
	return outboundConnector;
    }

    protected ExecutorService getBatchProcessors() {
	return batchProcessors;
    }

    /**
     * Processor that unmarshals an enriched event and forwards it to outbound
     * connector implementation.
     */
    protected class TopicBatchProcessor extends SystemUserRunnable {

	/** Partition */
	private TopicPartition topicPartition;

	/** Records to process */
	private List<ConsumerRecord<String, byte[]>> records;

	public TopicBatchProcessor(ITenantEngineLifecycleComponent component, TopicPartition topicPartition,
		List<ConsumerRecord<String, byte[]>> records) {
	    super(component);
	    this.topicPartition = topicPartition;
	    this.records = records;
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#runAsSystemUser()
	 */
	@Override
	public void runAsSystemUser() throws SiteWhereException {
	    List<IProcessedEventPayload> decoded = new ArrayList<>();
	    for (ConsumerRecord<String, byte[]> record : getRecords()) {
		try {
		    GProcessedEventPayload grpc = EventModelMarshaler.parseProcessedEventPayloadMessage(record.value());
		    ProcessedEventPayload payload = EventModelConverter.asApiProcessedEventPayload(grpc);
		    if (getLogger().isDebugEnabled()) {
			getLogger().debug("Received enriched event payload:\n\n"
				+ MarshalUtils.marshalJsonAsPrettyString(payload));
		    }
		    decoded.add(payload);
		} catch (SiteWhereException e) {
		    getLogger().error("Unable to parse outbound connector event payload.", e);
		} catch (Throwable e) {
		    getLogger().error("Unhandled exception parsing connector event payload.", e);
		}
	    }
	    try {
		getOutboundConnector().processEventBatch(decoded);
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