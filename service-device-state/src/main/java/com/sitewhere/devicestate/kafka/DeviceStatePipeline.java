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
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.Stores;

import com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine;
import com.sitewhere.grpc.kafka.serdes.SiteWhereSerdes;
import com.sitewhere.microservice.kafka.KafkaStreamPipeline;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Kafka pipeline for handling device state interactions
 */
public class DeviceStatePipeline extends KafkaStreamPipeline {

    /** Windowed store name */
    private static final String STORE_NAME = "aggregated-state";

    /** Number of seconds in window to aggregate events */
    private static final long WINDOW_LENGTH_IN_SECONDS = 5;

    /** Aggregates events for window into state object */
    private DeviceStateAggregator aggregator;

    /** Persists aggregated events */
    private DeviceStatePersistenceMapper deviceStatePersistenceMapper;

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
	builder.stream(getSourceTopicNames(), Consumed.with(Serdes.UUID(), SiteWhereSerdes.forProcessedEventPayload()))
		.groupByKey().windowedBy(TimeWindows.of(Duration.ofSeconds(WINDOW_LENGTH_IN_SECONDS)))
		.aggregate(() -> new AggregatedDeviceState(), getAggregator(),
			Materialized.as(
				Stores.inMemoryWindowStore(STORE_NAME, Duration.ofSeconds(WINDOW_LENGTH_IN_SECONDS * 3),
					Duration.ofSeconds(WINDOW_LENGTH_IN_SECONDS), false)))
		.toStream().map(getDeviceStatePersistenceMapper());
    }

    /*
     * @see
     * com.sitewhere.microservice.kafka.KafkaStreamPipeline#initialize(com.sitewhere
     * .spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.aggregator = new DeviceStateAggregator();
	this.deviceStatePersistenceMapper = new DeviceStatePersistenceMapper(
		((IDeviceStateTenantEngine) getTenantEngine()).getActiveConfiguration());

	super.initialize(monitor);
	initializeNestedComponent(getDeviceStatePersistenceMapper(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.microservice.kafka.KafkaStreamPipeline#start(com.sitewhere.spi.
     * microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);
	startNestedComponent(getDeviceStatePersistenceMapper(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.microservice.kafka.KafkaStreamPipeline#stop(com.sitewhere.spi.
     * microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);
	stopNestedComponent(getDeviceStatePersistenceMapper(), monitor);
    }

    protected DeviceStateAggregator getAggregator() {
	return aggregator;
    }

    protected DeviceStatePersistenceMapper getDeviceStatePersistenceMapper() {
	return deviceStatePersistenceMapper;
    }
}
