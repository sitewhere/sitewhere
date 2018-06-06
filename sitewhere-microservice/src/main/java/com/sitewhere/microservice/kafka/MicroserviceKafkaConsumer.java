/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Base class for components that consume messages from a Kafka topic.
 * 
 * @author Derek
 */
public abstract class MicroserviceKafkaConsumer extends TenantEngineLifecycleComponent
	implements IMicroserviceKafkaConsumer {

    /** Consumer */
    private KafkaConsumer<String, byte[]> consumer;

    /** Executor service */
    private ExecutorService executor;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getLogger().debug(
		"Consumer connecting to Kafka: " + getMicroservice().getInstanceSettings().getKafkaBootstrapServers());
	getLogger().info("Will be consuming messages from: " + getSourceTopicNames());
	this.consumer = new KafkaConsumer<>(buildConfiguration());
	this.executor = Executors.newSingleThreadExecutor(new MicroserviceConsumerThreadFactory());
	executor.execute(new MessageConsumer());
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
	if (getConsumer() != null) {
	    getConsumer().wakeup();
	}
	if (executor != null) {
	    executor.shutdown();
	}
    }

    /**
     * Build configuration settings used by Kafka streams.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected Properties buildConfiguration() throws SiteWhereException {
	Properties config = new Properties();
	config.put(ConsumerConfig.CLIENT_ID_CONFIG, getConsumerId());
	config.put(ConsumerConfig.GROUP_ID_CONFIG, getConsumerGroupId());
	config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
		getMicroservice().getInstanceSettings().getKafkaBootstrapServers());
	config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
	config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
	config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
	return config;
    }

    public KafkaConsumer<String, byte[]> getConsumer() {
	return consumer;
    }

    public void setConsumer(KafkaConsumer<String, byte[]> consumer) {
	this.consumer = consumer;
    }

    /**
     * Thread that polls Kafka for records arriving on the specified topic.
     * 
     * @author Derek
     */
    private class MessageConsumer implements Runnable {

	@Override
	public void run() {
	    try {
		getConsumer().subscribe(getSourceTopicNames());
		while (true) {
		    ConsumerRecords<String, byte[]> records = getConsumer().poll(Long.MAX_VALUE);

		    for (TopicPartition topicPartition : records.partitions()) {
			List<ConsumerRecord<String, byte[]>> topicRecords = records.records(topicPartition);
			processBatch(topicRecords);
			getConsumer().commitAsync(new OffsetCommitCallback() {
			    public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception e) {
				if (e != null) {
				    getLogger().error("Commit failed for offsets " + offsets, e);
				}
			    }
			});
		    }
		}
	    } catch (WakeupException e) {
		getLogger().info("Consumer thread received shutdown request.");
		getConsumer().unsubscribe();
	    } catch (Exception e) {
		getLogger().error("Error in consumer processing.", e);
	    } finally {
		getConsumer().close();
	    }
	}
    }

    /** Used for naming microservice consumer thread */
    private class MicroserviceConsumerThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Microservice Consumer " + counter.incrementAndGet());
	}
    }
}