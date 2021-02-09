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
package com.sitewhere.event.kafka;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;

import com.sitewhere.event.spi.kafka.IEventPersistencePipeline;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.kafka.serdes.SiteWhereSerdes;
import com.sitewhere.microservice.kafka.KafkaStreamPipeline;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Kafka pipeline for persisting inbound events (usually decoded from event
 * sources).
 */
public class EventPersistencePipeline extends KafkaStreamPipeline implements IEventPersistencePipeline {

    /** Applies event persistence logic to preprocessed event streams */
    private EventPersistenceMapper eventPersistenceMapper;

    /*
     * @see com.sitewhere.microservice.kafka.KafkaStreamPipeline#getPipelineName()
     */
    @Override
    public String getPipelineName() {
	return "persistence";
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
		getMicroservice().getKafkaTopicNaming().getInboundEventsTopic(getTenantEngine().getTenantResource()));
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
	builder.stream(getSourceTopicNames(),
		Consumed.with(Serdes.UUID(), SiteWhereSerdes.forPreprocessedEventPayload()))
		.map(getEventPersistenceMapper());
    }

    /*
     * @see
     * com.sitewhere.microservice.kafka.KafkaStreamPipeline#initialize(com.sitewhere
     * .spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.eventPersistenceMapper = new EventPersistenceMapper(
		((IEventManagementTenantEngine) getTenantEngine()).getActiveConfiguration());

	super.initialize(monitor);
	initializeNestedComponent(getEventPersistenceMapper(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.microservice.kafka.KafkaStreamPipeline#start(com.sitewhere.spi.
     * microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);
	startNestedComponent(getEventPersistenceMapper(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.microservice.kafka.KafkaStreamPipeline#stop(com.sitewhere.spi.
     * microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);
	stopNestedComponent(getEventPersistenceMapper(), monitor);
    }

    protected EventPersistenceMapper getEventPersistenceMapper() {
	return eventPersistenceMapper;
    }
}
