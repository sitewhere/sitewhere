/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.kafka;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;

import com.sitewhere.grpc.kafka.serdes.SiteWhereSerdes;
import com.sitewhere.microservice.kafka.KafkaStreamPipeline;

/**
 * Kafka pipeline for handling device state interactions
 */
public class DeviceStatePipeline extends KafkaStreamPipeline {

    /*
     * @see com.sitewhere.microservice.kafka.KafkaStreamPipeline#getPipelineName()
     */
    @Override
    public String getPipelineName() {
	return "events";
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IKafkaStreamPipeline#getSourceTopicNames
     * ()
     */
    @Override
    public List<String> getSourceTopicNames() {
	List<String> topics = new ArrayList<>();
	topics.add(
		getMicroservice().getKafkaTopicNaming().getOutboundEventsTopic(getTenantEngine().getTenantResource()));
	return topics;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.IKafkaStreamPipeline#buildStreams(org.
     * apache.kafka.streams.StreamsBuilder)
     */
    @Override
    public void buildStreams(StreamsBuilder builder) {
	builder.stream(getSourceTopicNames(), Consumed.with(Serdes.UUID(), SiteWhereSerdes.forEnrichedEventPayload()))
		.process(new DeviceStatePersistenceProcessorSupplier(), new String[0]);
    }
}
