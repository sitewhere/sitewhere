/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.InvalidReplicationFactorException;
import org.apache.kafka.common.errors.RetriableException;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Base class for components that produce messages that are forwarded to a Kafka
 * topic.
 * 
 * @author Derek
 */
public abstract class MicroserviceKafkaProducer extends TenantEngineLifecycleComponent
	implements IMicroserviceKafkaProducer {

    /** Kafka availability check interval */
    private static final int KAFKA_RETRY_INTERVAL_MS = 10 * 1000;

    /** Producer */
    private KafkaProducer<String, byte[]> producer;

    /** Kafka admin client */
    private AdminClient kafkaAdmin;

    /** Kafka acknowledgement policy */
    private AckPolicy ackPolicy;

    /** Indicator for whether Kafka is available */
    private CountDownLatch kafkaAvailable;

    /** Executor service for waiter thread */
    ExecutorService waiterService;

    public MicroserviceKafkaProducer(AckPolicy ackPolicy) {
	this.ackPolicy = ackPolicy;
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
		"Producer connecting to Kafka: " + getMicroservice().getInstanceSettings().getKafkaBootstrapServers());
	getLogger().info("Will be producing messages for: " + getTargetTopicName());
	this.producer = new KafkaProducer<String, byte[]>(buildConfiguration());
	this.kafkaAdmin = AdminClient.create(buildAdminConfiguration());
	this.kafkaAvailable = new CountDownLatch(1);
	this.waiterService = Executors.newSingleThreadExecutor();
	getWaiterService().execute(new KafkaWaiter());
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
	if (getProducer() != null) {
	    getProducer().close();
	}
	if (getWaiterService() != null) {
	    getWaiterService().shutdown();
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer#send(java.
     * lang.String, byte[])
     */
    @Override
    public Future<RecordMetadata> send(String key, byte[] message) throws SiteWhereException {
	while (true) {
	    ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(getTargetTopicName(), key,
		    message);
	    try {
		if (getKafkaAvailable().getCount() != 0) {
		    getLogger().info("Producer waiting on Kafka to become available...");
		}
		getKafkaAvailable().await();
		return getProducer().send(record);
	    } catch (RetriableException e) {
		// Wait before attempting to send again.
		try {
		    getLogger().info(
			    String.format("Got retriable exception [%s] while sending Kafka payload. Waiting to retry.",
				    e.getMessage()));
		    Thread.sleep(5000);
		} catch (InterruptedException e1) {
		    getLogger().info("Interrupted while waiting to send Kafka payload.");
		}
	    } catch (InterruptedException e) {
		throw new SiteWhereException("Producer interrupted while waiting for Kafka.", e);
	    } catch (IllegalStateException e) {
		throw new SiteWhereException("Producer unable to send record.", e);
	    } catch (Throwable e) {
		throw new SiteWhereException("Unhandled exception in producer while sending record.", e);
	    }
	}
    }

    /**
     * Build configuration settings used by producer.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected Properties buildConfiguration() throws SiteWhereException {
	Properties config = new Properties();
	config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
		getMicroservice().getInstanceSettings().getKafkaBootstrapServers());
	config.put(ProducerConfig.ACKS_CONFIG, getAckPolicy().getConfig());
	config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
	config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
	return config;
    }

    /**
     * Build configuration settings used by admin client.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected Properties buildAdminConfiguration() throws SiteWhereException {
	Properties config = new Properties();
	config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
		getMicroservice().getInstanceSettings().getKafkaBootstrapServers());
	return config;
    }

    /**
     * Thread that waits for Kafka to become available.
     */
    private class KafkaWaiter implements Runnable {

	@Override
	public void run() {
	    getLogger().info("Attempting to connect to Kafka...");
	    while (true) {
		try {
		    Map<String, TopicDescription> topicMap = getKafkaAdmin()
			    .describeTopics(Arrays.asList(getTargetTopicName())).all().get();
		    TopicDescription topic = topicMap.get(getTargetTopicName());
		    if (topic != null) {
			getLogger().info("Kafka detected as available.");
			getKafkaAvailable().countDown();
			return;
		    }
		} catch (ExecutionException e) {
		    Throwable t = e.getCause();
		    if (t instanceof UnknownTopicOrPartitionException) {
			try {
			    IInstanceSettings settings = getMicroservice().getInstanceSettings();
			    NewTopic newTopic = new NewTopic(getTargetTopicName(),
				    settings.getKafkaDefaultTopicPartitions(),
				    (short) settings.getKafkaDefaultTopicReplicationFactor());
			    CreateTopicsResult result = getKafkaAdmin()
				    .createTopics(Collections.singletonList(newTopic));
			    result.all().get();
			    getLogger().info(String.format("Kafka topic '%s' created.", getTargetTopicName()));
			} catch (SiteWhereException e1) {
			    getLogger().error("Exception creating topic.", e1);
			} catch (ExecutionException e1) {
			    if (e1.getCause() instanceof TopicExistsException) {
				getLogger().debug("Topic already existed.");
			    } else if (e1.getCause() instanceof InvalidReplicationFactorException) {
				getLogger().info("Not enough replicas are available to create topic. Waiting.");
				try {
				    Thread.sleep(1000);
				} catch (InterruptedException e2) {
				    getLogger().error("Interrupted while waiting for replicas.");
				    return;
				}
			    } else {
				getLogger().error("Kakfa exception creating topic.", e1);
			    }
			} catch (InterruptedException e1) {
			    getLogger().error("Interrupted while creating topic.");
			    return;
			}
		    } else {
			getLogger()
				.warn("Execution exception connecting to Kafka. Will continue attempting to connect. ("
					+ e.getMessage() + ")", t);
		    }
		} catch (Throwable t) {
		    getLogger().warn("Exception while connecting to Kafka. Will continue attempting to connect.", t);
		}
		try {
		    Thread.sleep(KAFKA_RETRY_INTERVAL_MS);
		} catch (InterruptedException e) {
		    getLogger().warn("Interrupted while waiting for Kafka to become available.");
		    return;
		}
	    }
	}
    }

    protected KafkaProducer<String, byte[]> getProducer() {
	return producer;
    }

    protected void setProducer(KafkaProducer<String, byte[]> producer) {
	this.producer = producer;
    }

    protected AdminClient getKafkaAdmin() {
	return kafkaAdmin;
    }

    protected void setKafkaAdmin(AdminClient kafkaAdmin) {
	this.kafkaAdmin = kafkaAdmin;
    }

    protected AckPolicy getAckPolicy() {
	return ackPolicy;
    }

    protected void setAckPolicy(AckPolicy ackPolicy) {
	this.ackPolicy = ackPolicy;
    }

    protected CountDownLatch getKafkaAvailable() {
	return kafkaAvailable;
    }

    protected void setKafkaAvailable(CountDownLatch kafkaAvailable) {
	this.kafkaAvailable = kafkaAvailable;
    }

    protected ExecutorService getWaiterService() {
	return waiterService;
    }

    protected void setWaiterService(ExecutorService waiterService) {
	this.waiterService = waiterService;
    }
}