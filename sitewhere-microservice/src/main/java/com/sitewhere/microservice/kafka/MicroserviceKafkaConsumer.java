/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Base class for components that consume messages from a Kafka topic.
 * 
 * @author Derek
 */
public abstract class MicroserviceKafkaConsumer extends TenantLifecycleComponent implements IMicroserviceKafkaConsumer {

    /** Consumer */
    private KafkaConsumer<String, byte[]> consumer;

    /** Executor service */
    private ExecutorService executor;

    /** Parent microservice */
    private IMicroservice microservice;

    public MicroserviceKafkaConsumer(IMicroservice microservice) {
	this.microservice = microservice;
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
	getLogger().info(
		"Consumer connecting to Kafka: " + getMicroservice().getInstanceSettings().getKafkaBootstrapServers());
	getLogger().info("Will be consuming messages from: " + getSourceTopicName());
	this.consumer = new KafkaConsumer<>(buildConfiguration());
	this.executor = Executors.newSingleThreadExecutor();
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
	getConsumer().wakeup();
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
	return config;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#
     * getMicroservice()
     */
    @Override
    public IMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroservice microservice) {
	this.microservice = microservice;
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
		getConsumer().subscribe(Collections.singletonList(getSourceTopicName()));
		while (true) {
		    ConsumerRecords<String, byte[]> records = consumer.poll(Long.MAX_VALUE);
		    getLogger().debug("Received {} records from {}", records.count(), getSourceTopicName());

		    for (TopicPartition topicPartition : records.partitions()) {
			List<ConsumerRecord<String, byte[]>> topicRecords = records.records(topicPartition);
			for (ConsumerRecord<String, byte[]> record : topicRecords) {
			    getLogger().debug(
				    "ConsumerId:{}-Topic:{} => Partition={}, Offset={}, EventTime:[{}] Val={}",
				    getConsumerId(), topicPartition.topic(), record.partition(), record.offset(),
				    record.timestamp(), record.value());
			    received(record.key(), record.value());
			}

			long lastPartitionOffset = topicRecords.get(topicRecords.size() - 1).offset();
			getConsumer().commitSync(Collections.singletonMap(topicPartition,
				new OffsetAndMetadata(lastPartitionOffset + 1)));
		    }
		}
	    } catch (WakeupException e) {
		getLogger().info("Consumer thread received shutdown request.");
	    } catch (Exception e) {
		getLogger().error("Error in consumer processing.", e);
	    } finally {
		getConsumer().close();
	    }
	}
    }
}