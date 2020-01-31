/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;

import com.sitewhere.grpc.client.device.DeviceModelConverter;
import com.sitewhere.grpc.client.device.DeviceModelMarshaler;
import com.sitewhere.grpc.model.DeviceModel.GDeviceRegistationPayload;
import com.sitewhere.microservice.kafka.DirectKafkaConsumer;
import com.sitewhere.microservice.security.SystemUserRunnable;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.registration.spi.kafka.IDeviceRegistrationEventsConsumer;
import com.sitewhere.registration.spi.microservice.IDeviceRegistrationTenantEngine;
import com.sitewhere.rest.model.device.event.kafka.DeviceRegistrationPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Listens on Kafka topic for device registration events, forwarding them to the
 * registration manager.
 */
public class DeviceRegistrationEventsConsumer extends DirectKafkaConsumer implements IDeviceRegistrationEventsConsumer {

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Suffix for group id */
    private static String GROUP_ID_SUFFIX = "device-registration-event-consumers";

    /** Number of threads processing new device registrations */
    private static final int CONCURRENT_REGISTRATION_PROCESSING_THREADS = 2;

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
	return getMicroservice().getKafkaTopicNaming().getTenantPrefix(getTenantEngine().getTenantResource())
		+ GROUP_ID_SUFFIX;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#
     * getSourceTopicNames()
     */
    @Override
    public List<String> getSourceTopicNames() throws SiteWhereException {
	List<String> topics = new ArrayList<String>();
	topics.add(getMicroservice().getKafkaTopicNaming()
		.getDeviceRegistrationEventsTopic(getTenantEngine().getTenantResource()));
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
	executor = Executors.newFixedThreadPool(CONCURRENT_REGISTRATION_PROCESSING_THREADS,
		new DeviceRegistrationProcessorThreadFactory());
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
	executor.execute(new DeviceRegistrationProcessor(this, message));
    }

    /**
     * Processor that unmarshals a device registration payload and hands it off to
     * the registration manager.
     */
    protected class DeviceRegistrationProcessor extends SystemUserRunnable {

	/** Encoded payload */
	private byte[] encoded;

	public DeviceRegistrationProcessor(ITenantEngineLifecycleComponent component, byte[] encoded) {
	    super(component);
	    this.encoded = encoded;
	}

	/*
	 * @see com.sitewhere.microservice.security.SystemUserRunnable#
	 * runAsSystemUser()
	 */
	@Override
	public void runAsSystemUser() throws SiteWhereException {
	    try {
		GDeviceRegistationPayload grpc = DeviceModelMarshaler.parseDeviceRegistrationPayloadMessage(encoded);
		DeviceRegistrationPayload payload = DeviceModelConverter.asApiDeviceRegistrationPayload(grpc);
		if (getLogger().isDebugEnabled()) {
		    getLogger().debug(
			    "Received registration for device:\n\n" + MarshalUtils.marshalJsonAsPrettyString(payload));
		}

		// Pass payload to registration manager.
		((IDeviceRegistrationTenantEngine) getTenantEngine()).getRegistrationManager()
			.handleDeviceRegistration(payload);
	    } catch (SiteWhereException e) {
		getLogger().error("Error processing device registration.", e);
	    } catch (Throwable e) {
		getLogger().error("Unhandled exception processing device registration.", e);
	    }
	}
    }

    /** Used for naming device registration processing threads */
    private class DeviceRegistrationProcessorThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Device Registration Processor " + counter.incrementAndGet());
	}
    }
}
