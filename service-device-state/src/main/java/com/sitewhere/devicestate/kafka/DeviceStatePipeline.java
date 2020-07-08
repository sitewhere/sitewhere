/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.kafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.Stores;

import com.sitewhere.devicestate.configuration.DeviceStateTenantConfiguration;
import com.sitewhere.grpc.kafka.serdes.SiteWhereSerdes;
import com.sitewhere.microservice.kafka.KafkaStreamPipeline;

/**
 * Kafka pipeline for handling device state interactions
 */
public class DeviceStatePipeline extends KafkaStreamPipeline {

    /** Windowed store name */
    private static final String STORE_NAME = "aggregated-state";

    /** Number of seconds in window to aggregate events */
    private static final long WINDOW_LENGTH_IN_SECONDS = 5;

    /** Tenant configuration for device state */
    private DeviceStateTenantConfiguration configuration;

    /** Aggregates events for window into state object */
    private DeviceStateAggregator aggregator = new DeviceStateAggregator();

    public DeviceStatePipeline(DeviceStateTenantConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see com.sitewhere.microservice.kafka.KafkaStreamPipeline#getPipelineName()
     */
    @Override
    public String getPipelineName() {
	return "events";
    }

    /*
     * @see com.sitewhere.microservice.kafka.KafkaStreamPipeline#
     * getDefaultValueSerdeClass()
     */
    @Override
    public Class<?> getDefaultValueSerdeClass() {
	return AggregatedDeviceStateSerde.class;
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
		.groupByKey().windowedBy(TimeWindows.of(Duration.ofSeconds(WINDOW_LENGTH_IN_SECONDS)))
		.aggregate(() -> new AggregatedDeviceState(), getAggregator(),
			Materialized.as(
				Stores.inMemoryWindowStore(STORE_NAME, Duration.ofSeconds(WINDOW_LENGTH_IN_SECONDS * 4),
					Duration.ofSeconds(WINDOW_LENGTH_IN_SECONDS), false)))
		.toStream().print(Printed.toSysOut());
    }

    protected DeviceStateTenantConfiguration getConfiguration() {
	return configuration;
    }

    protected DeviceStateAggregator getAggregator() {
	return aggregator;
    }
}
