/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka;

import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
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
	waitForKafkaAvailable();
    }

    /**
     * Block startup until Kafka is considered available.
     */
    protected void waitForKafkaAvailable() {
	getLogger().info("Attempting to connect to Kafka...");
	while (true) {
	    try {
		getKafkaAdmin().listTopics().names().get();
		getLogger().info("Kafka detected as available.");
		return;
	    } catch (Throwable t) {
		getLogger().info("Kafka not detected. Will continue attempting to connect. (" + t.getMessage() + ")");
	    }
	    try {
		Thread.sleep(KAFKA_RETRY_INTERVAL_MS);
	    } catch (InterruptedException e) {
		getLogger().warn("Interrupted while waiting for Kafka to become available.");
		return;
	    }
	}
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
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaProducer#send(java.
     * lang.String, byte[])
     */
    @Override
    public Future<RecordMetadata> send(String key, byte[] message) throws SiteWhereException {
	ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(getTargetTopicName(), key, message);
	try {
	    return getProducer().send(record);
	} catch (IllegalStateException e) {
	    throw new SiteWhereException("Producer unable to send record.", e);
	} catch (Throwable e) {
	    throw new SiteWhereException("Unhandled exception in producer while sending record.", e);
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
}