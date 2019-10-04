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

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.grpc.client.event.EventModelConverter;
import com.sitewhere.grpc.client.event.EventModelMarshaler;
import com.sitewhere.grpc.model.DeviceEventModel.GDecodedEventPayload;
import com.sitewhere.inbound.processing.InboundPayloadProcessingLogic;
import com.sitewhere.inbound.spi.kafka.IDecodedEventsConsumer;
import com.sitewhere.inbound.spi.processing.IInboundPayloadProcessingLogic;
import com.sitewhere.inbound.spi.processing.IInboundProcessingConfiguration;
import com.sitewhere.rest.model.device.event.kafka.DecodedEventPayload;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Listens on Kafka topic for decoded events, making them available for inbound
 * processing.
 */
public class DecodedEventsConsumer extends MicroserviceKafkaConsumer implements IDecodedEventsConsumer {

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Suffix for group id */
    private static String GROUP_ID_SUFFIX = "decoded-event-consumers";

    /** Get settings for inbound processing */
    private IInboundProcessingConfiguration inboundProcessingConfiguration;

    /** Inbound payload processing logic */
    private IInboundPayloadProcessingLogic inboundPayloadProcessingLogic;

    public DecodedEventsConsumer(IInboundProcessingConfiguration inboundProcessingConfiguration) {
	this.inboundProcessingConfiguration = inboundProcessingConfiguration;
	this.inboundPayloadProcessingLogic = new InboundPayloadProcessingLogic(this);
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
	topics.add(getMicroservice().getKafkaTopicNaming()
		.getEventSourceDecodedEventsTopic(getTenantEngine().getTenant()));
	topics.add(
		getMicroservice().getKafkaTopicNaming().getInboundReprocessEventsTopic(getTenantEngine().getTenant()));
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
	init.addInitializeStep(this, getInboundPayloadProcessingLogic(), true);

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
	start.addStartStep(this, getInboundPayloadProcessingLogic(), true);

	// Execute startup steps.
	start.execute(monitor);

	getLogger().info("Allocating " + getInboundProcessingConfiguration().getProcessingThreadCount()
		+ " threads for inbound event processing.");
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
	stop.addStopStep(this, getInboundPayloadProcessingLogic());

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
	if (getLogger().isDebugEnabled()) {
	    getLogger().debug(String.format("Received %d records from decoded events consumer.", records.size()));
	}
	try {
	    List<GDecodedEventPayload> decoded = new ArrayList<>();
	    for (ConsumerRecord<String, byte[]> record : records) {
		GDecodedEventPayload message = EventModelMarshaler.parseDecodedEventPayloadMessage(record.value());
		if (getLogger().isDebugEnabled()) {
		    DecodedEventPayload payload = EventModelConverter.asApiDecodedEventPayload(message);
		    getLogger().debug(
			    "Received decoded event payload:\n\n" + MarshalUtils.marshalJsonAsPrettyString(payload));
		}
		decoded.add(message);
	    }
	    getInboundPayloadProcessingLogic().process(topicPartition, decoded);
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
     * getInboundPayloadProcessingLogic()
     */
    @Override
    public IInboundPayloadProcessingLogic getInboundPayloadProcessingLogic() {
	return inboundPayloadProcessingLogic;
    }

    public void setInboundPayloadProcessingLogic(IInboundPayloadProcessingLogic inboundPayloadProcessingLogic) {
	this.inboundPayloadProcessingLogic = inboundPayloadProcessingLogic;
    }

    /*
     * @see com.sitewhere.inbound.spi.kafka.IDecodedEventsConsumer#
     * getInboundProcessingConfiguration()
     */
    @Override
    public IInboundProcessingConfiguration getInboundProcessingConfiguration() {
	return inboundProcessingConfiguration;
    }
}