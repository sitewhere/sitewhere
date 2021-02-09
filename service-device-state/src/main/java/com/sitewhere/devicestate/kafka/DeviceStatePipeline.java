/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
