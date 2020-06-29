/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.event.processing.EventManagementStoreLogic;
import com.sitewhere.event.spi.processing.IEventManagementConfiguration;
import com.sitewhere.event.spi.processing.IEventManagementStoreLogic;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.event.spi.kafka.IInboundEventsConsumer;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.client.common.converter.CommonModelConverter;
import com.sitewhere.grpc.client.event.EventModelConverter;
import com.sitewhere.grpc.client.event.EventModelMarshaler;
import com.sitewhere.grpc.model.DeviceEventModel.GAnyDeviceEventCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GPreprocessedEventPayload;
import com.sitewhere.rest.model.device.event.kafka.PreprocessedEventPayload;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;

/**
 * Kafka consumer which takes events that have been pre-processed by inbound
 * processing and persists them via the event management APIs.
 */
public class InboundEventsConsumer extends MicroserviceKafkaConsumer implements IInboundEventsConsumer {

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Suffix for group id */
    private static String GROUP_ID_SUFFIX = "inbound-event-consumers";

	/** Get settings for inbound processing */
	private IEventManagementConfiguration eventManagementConfiguration;

	/** Inbound payload processing logic */
	private IEventManagementStoreLogic eventManagementStoreLogic;

	public InboundEventsConsumer(IEventManagementConfiguration eventManagementConfiguration) {
		this.eventManagementConfiguration = eventManagementConfiguration;
		this.eventManagementStoreLogic = new EventManagementStoreLogic(this);
	}
	public InboundEventsConsumer() {
		this.eventManagementStoreLogic = new EventManagementStoreLogic(this);
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
	topics.add(getMicroservice().getKafkaTopicNaming().getInboundEventsTopic(getTenantEngine().getTenant()));
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

		// Create step that will initialize components.
		ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

		// Initialize inbound processing logic.
		init.addInitializeStep(this, getEventManagementStoreLogic(), true);

		// Execute initialization steps.
		init.execute(monitor);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer#start(com.
	 * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
	 */
	@Override
	public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		// Create step that will start components.
		ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

		// Start inbound processing logic.
		start.addStartStep(this, getEventManagementStoreLogic(), true);

		// Execute startup steps.
		start.execute(monitor);

		getLogger().info("Allocating 25 threads for inbound event processing.");
		super.start(monitor);
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

		// Create step that will stop components.
		ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

		// Stop inbound processing logic.
		stop.addStopStep(this, getEventManagementStoreLogic());

		// Execute shutdown steps.
		stop.execute(monitor);
	}

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IMicroserviceKafkaConsumer#process(org.
     * apache.kafka.common.TopicPartition, java.util.List)
     */
    @Override
    public void process(TopicPartition topicPartition, List<ConsumerRecord<String, byte[]>> records) {
	try {
	    List<GPreprocessedEventPayload> preprocessed = new ArrayList<>();
	    for (ConsumerRecord<String, byte[]> record : records) {
		GPreprocessedEventPayload message = EventModelMarshaler
			.parsePreprocessedEventPayloadMessage(record.value());
		if (getLogger().isDebugEnabled()) {
		    PreprocessedEventPayload payload = EventModelConverter.asApiPreprocessedEventPayload(message);
		    getLogger().debug("Received event payload:\n\n" + MarshalUtils.marshalJsonAsPrettyString(payload));
		}
		preprocessed.add(message);
	    }
	    //storeEvents(preprocessed);
		getEventManagementStoreLogic().process(topicPartition, preprocessed);
	    getConsumer().commitAsync(new OffsetCommitCallback() {
		public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception e) {
		    if (e != null) {
			getLogger().error("Commit failed for offsets " + offsets, e);
		    }
		}
	    });
	} catch (SiteWhereException e) {
	    getLogger().error("Inbound processing for event batch failed.", e);
	}
    }

	/*
	 * @see com.sitewhere.inbound.spi.kafka.IDecodedEventsConsumer#
	 * getEventManagementProcessingLogic()
	 */
	@Override
	public IEventManagementStoreLogic getEventManagementStoreLogic() {
		return eventManagementStoreLogic;
	}

	public void setEventManagementStoreLogic(IEventManagementStoreLogic eventManagementStoreLogic) {
		this.eventManagementStoreLogic = eventManagementStoreLogic;
	}

	/*
	 * @see com.sitewhere.inbound.spi.kafka.IDecodedEventsConsumer#
	 * getInboundProcessingConfiguration()
	 */
	@Override
	public IEventManagementConfiguration getEventManagementConfiguration() {
		return eventManagementConfiguration;
	}
}
