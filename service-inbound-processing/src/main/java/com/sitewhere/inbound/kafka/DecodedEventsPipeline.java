/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.kafka;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;

import com.sitewhere.grpc.kafka.serdes.SiteWhereSerdes;
import com.sitewhere.inbound.spi.kafka.IDecodedEventsPipeline;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine;
import com.sitewhere.microservice.kafka.KafkaStreamPipeline;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Kafka pipeline for handling decoded events (usually from event sources).
 */
public class DecodedEventsPipeline extends KafkaStreamPipeline implements IDecodedEventsPipeline {

    /** Applies event processing logic to inbound event streams */
    private InboundEventProcessingSupplier inboundEventProcessingSupplier;

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IKafkaStreamPipeline#getSourceTopicNames
     * ()
     */
    @Override
    public List<String> getSourceTopicNames() {
	List<String> topics = new ArrayList<>();
	topics.add(getMicroservice().getKafkaTopicNaming()
		.getEventSourceDecodedEventsTopic(getTenantEngine().getTenantResource()));
	topics.add(getMicroservice().getKafkaTopicNaming()
		.getInboundReprocessEventsTopic(getTenantEngine().getTenantResource()));
	return topics;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IKafkaStreamPipeline#buildStreams(org.
     * apache.kafka.streams.StreamsBuilder)
     */
    @Override
    public void buildStreams(StreamsBuilder builder) {
	// Pipeline handles both event source decoded events and reprocess events.
	builder.stream(getSourceTopicNames(), Consumed.with(Serdes.String(), SiteWhereSerdes.forDecodedEventPayload()))
		.process(getInboundEventProcessingSupplier(), new String[0]);
    }

    /*
     * @see
     * com.sitewhere.microservice.kafka.KafkaStreamPipeline#initialize(com.sitewhere
     * .spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.inboundEventProcessingSupplier = new InboundEventProcessingSupplier(
		((IInboundProcessingTenantEngine) getTenantEngine()).getActiveConfiguration());

	super.initialize(monitor);
	initializeNestedComponent(getInboundEventProcessingSupplier(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.microservice.kafka.KafkaStreamPipeline#start(com.sitewhere.spi.
     * microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);
	startNestedComponent(getInboundEventProcessingSupplier(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.microservice.kafka.KafkaStreamPipeline#stop(com.sitewhere.spi.
     * microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);
	stopNestedComponent(getInboundEventProcessingSupplier(), monitor);
    }

    protected InboundEventProcessingSupplier getInboundEventProcessingSupplier() {
	return inboundEventProcessingSupplier;
    }
}
