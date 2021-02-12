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
package com.sitewhere.inbound.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Produced;

import com.sitewhere.grpc.kafka.serdes.SiteWhereSerdes;
import com.sitewhere.grpc.model.DeviceEventModel.GDecodedEventPayload;
import com.sitewhere.inbound.spi.kafka.IDecodedEventsPipeline;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine;
import com.sitewhere.microservice.kafka.KafkaStreamPipeline;
import com.sitewhere.microservice.kafka.KafkaTopicWaiter;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Kafka pipeline for handling decoded events (usually from event sources).
 */
public class DecodedEventsPipeline extends KafkaStreamPipeline implements IDecodedEventsPipeline {

    /** Mapper that looks up a device by token */
    private DeviceLookupMapper deviceLookupMapper;

    /** Mapper that looks up active assignments for a device */
    private DeviceAssignmentsLookupMapper deviceAssignmentsLookupMapper;

    /** Mapper that builds preprocessed events from lookup data */
    private PreprocessedEventMapper preprocessedEventMapper;

    /** Indicator for whether unregistered device topic is available */
    private CountDownLatch unregisteredDeviceTopicAvailable = new CountDownLatch(1);

    /** Indicator for whether inbound events topic is available */
    private CountDownLatch inboundEventsTopicAvailable = new CountDownLatch(1);

    /** Executor service for waiter thread */
    ExecutorService waiterService;

    /*
     * @see com.sitewhere.microservice.kafka.KafkaStreamPipeline#getPipelineName()
     */
    @Override
    public String getPipelineName() {
	return "decoded";
    }

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
    @SuppressWarnings("unchecked")
    public void buildStreams(StreamsBuilder builder) {
	// Pipeline handles both event source decoded events and reprocess events.
	KStream<String, GDecodedEventPayload> input = builder.stream(getSourceTopicNames(),
		Consumed.with(Serdes.String(), SiteWhereSerdes.forDecodedEventPayload()));

	// Attempt to resolve device and split stream based on resolution.
	KStream<UUID, InboundEventContext> deviceResolver = input.map(getDeviceLookupMapper());

	// Create branches based on whether device was resolved.
	Predicate<UUID, InboundEventContext> deviceFound = (key, value) -> value.getDevice() != null;
	Predicate<UUID, InboundEventContext> deviceNotFound = (key, value) -> true;
	KStream<UUID, InboundEventContext>[] deviceBranches = deviceResolver.branch(deviceFound, deviceNotFound);
	KStream<UUID, InboundEventContext> deviceFoundBranch = deviceBranches[0];
	KStream<UUID, InboundEventContext> deviceNotFoundBranch = deviceBranches[1];

	// Route unresolved device tokens to their own topic.
	String unregisteredTopic = getMicroservice().getKafkaTopicNaming()
		.getUnregisteredDeviceEventsTopic(getTenantEngine().getTenantResource());
	deviceNotFoundBranch.mapValues((key, value) -> value.getDecodedEventPayload()).to(unregisteredTopic,
		Produced.with(Serdes.UUID(), SiteWhereSerdes.forDecodedEventPayload()));

	// Create separate "preprocessed event" payloads for each assignment and
	// forward to inbound events topic.
	String inboundEventsTopic = getMicroservice().getKafkaTopicNaming()
		.getInboundEventsTopic(getTenantEngine().getTenantResource());
	deviceFoundBranch.map(getDeviceAssignmentsLookupMapper()).map(getPreprocessedEventMapper())
		.flatMapValues((key, value) -> value)
		.to(inboundEventsTopic, Produced.with(Serdes.UUID(), SiteWhereSerdes.forPreprocessedEventPayload()));
    }

    /*
     * @see
     * com.sitewhere.microservice.kafka.KafkaStreamPipeline#initialize(com.sitewhere
     * .spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.deviceLookupMapper = new DeviceLookupMapper(
		((IInboundProcessingTenantEngine) getTenantEngine()).getActiveConfiguration());
	this.deviceAssignmentsLookupMapper = new DeviceAssignmentsLookupMapper(
		((IInboundProcessingTenantEngine) getTenantEngine()).getActiveConfiguration());
	this.preprocessedEventMapper = new PreprocessedEventMapper(
		((IInboundProcessingTenantEngine) getTenantEngine()).getActiveConfiguration());

	super.initialize(monitor);
	initializeNestedComponent(getDeviceLookupMapper(), monitor, true);
	initializeNestedComponent(getDeviceAssignmentsLookupMapper(), monitor, true);
	initializeNestedComponent(getPreprocessedEventMapper(), monitor, true);
    }

    /**
     * Wait for Kafka topics to be validated/created.
     * 
     * @throws SiteWhereException
     */
    protected void waitForTopics() throws SiteWhereException {
	try {
	    this.waiterService = Executors.newSingleThreadExecutor();

	    // Validate or create inbound events topic.
	    getWaiterService().execute(new InboundEventsTopicWaiter(this));
	    getInboundEventsTopicAvailable().await();

	    // Validate or create
	    getWaiterService().execute(new UnregisteredDeviceTopicWaiter(this));
	    getUnregisteredDeviceTopicAvailable().await();

	    getWaiterService().shutdown();
	} catch (InterruptedException e) {
	    throw new SiteWhereException("Interrupted while waiting for topics to be validated/created.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.microservice.kafka.KafkaStreamPipeline#start(com.sitewhere.spi.
     * microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);
	startNestedComponent(getDeviceLookupMapper(), monitor, true);
	startNestedComponent(getDeviceAssignmentsLookupMapper(), monitor, true);
	startNestedComponent(getPreprocessedEventMapper(), monitor, true);

	// Wait for Kafka topics to be created/validated.
	waitForTopics();
    }

    /*
     * @see
     * com.sitewhere.microservice.kafka.KafkaStreamPipeline#stop(com.sitewhere.spi.
     * microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);
	stopNestedComponent(getPreprocessedEventMapper(), monitor);
	stopNestedComponent(getDeviceAssignmentsLookupMapper(), monitor);
	stopNestedComponent(getDeviceLookupMapper(), monitor);
    }

    /**
     * Thread that waits for inbound events topic to become available.
     */
    private class InboundEventsTopicWaiter extends KafkaTopicWaiter {

	public InboundEventsTopicWaiter(ITenantEngineLifecycleComponent component) {
	    super(component, component.getMicroservice().getKafkaTopicNaming()
		    .getInboundEventsTopic(component.getTenantEngine().getTenantResource()));
	}

	/*
	 * @see com.sitewhere.microservice.kafka.KafkaTopicWaiter#onTopicAvailable()
	 */
	@Override
	protected void onTopicAvailable() {
	    getInboundEventsTopicAvailable().countDown();
	}
    }

    /**
     * Thread that waits for unregistered device topic to become available.
     */
    private class UnregisteredDeviceTopicWaiter extends KafkaTopicWaiter {

	public UnregisteredDeviceTopicWaiter(ITenantEngineLifecycleComponent component) {
	    super(component, component.getMicroservice().getKafkaTopicNaming()
		    .getUnregisteredDeviceEventsTopic(component.getTenantEngine().getTenantResource()));
	}

	/*
	 * @see com.sitewhere.microservice.kafka.KafkaTopicWaiter#onTopicAvailable()
	 */
	@Override
	protected void onTopicAvailable() {
	    getUnregisteredDeviceTopicAvailable().countDown();
	}
    }

    protected DeviceLookupMapper getDeviceLookupMapper() {
	return deviceLookupMapper;
    }

    protected DeviceAssignmentsLookupMapper getDeviceAssignmentsLookupMapper() {
	return deviceAssignmentsLookupMapper;
    }

    protected PreprocessedEventMapper getPreprocessedEventMapper() {
	return preprocessedEventMapper;
    }

    protected CountDownLatch getUnregisteredDeviceTopicAvailable() {
	return unregisteredDeviceTopicAvailable;
    }

    protected CountDownLatch getInboundEventsTopicAvailable() {
	return inboundEventsTopicAvailable;
    }

    protected ExecutorService getWaiterService() {
	return waiterService;
    }
}
