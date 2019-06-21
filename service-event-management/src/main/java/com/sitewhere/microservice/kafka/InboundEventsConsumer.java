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
import com.sitewhere.event.spi.kafka.IInboundEventsConsumer;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.client.common.converter.CommonModelConverter;
import com.sitewhere.grpc.client.event.EventModelConverter;
import com.sitewhere.grpc.client.event.EventModelMarshaler;
import com.sitewhere.grpc.model.DeviceEventModel.GAnyDeviceEventCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GPreprocessedEventPayload;
import com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer;
import com.sitewhere.rest.model.device.event.kafka.PreprocessedEventPayload;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;

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

    /** Counter for processed events */
    private Gauge kafkaBatchSize = TenantEngineLifecycleComponent.createGaugeMetric("inbound_events_kafka_batch_size",
	    "Size of Kafka inbound event batches");

    /** Counter for processed events */
    private Counter processedEvents = TenantEngineLifecycleComponent.createCounterMetric("inbound_events_event_count",
	    "Count of total events processed by consumer");

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
	    storeEvents(preprocessed);
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

    /**
     * Store a batch of events via the event management APIs.
     * 
     * @param events
     * @throws SiteWhereException
     */
    protected void storeEvents(List<GPreprocessedEventPayload> payloads) throws SiteWhereException {
	getKafkaBatchSize().labels(buildLabels()).set(payloads.size());
	for (GPreprocessedEventPayload payload : payloads) {
	    GAnyDeviceEventCreateRequest grpc = payload.getEvent();
	    UUID assignmentId = CommonModelConverter.asApiUuid(payload.getDeviceAssignmentId());
	    IDeviceEventCreateRequest request = EventModelConverter.asApiDeviceEventCreateRequest(grpc);
	    switch (request.getEventType()) {
	    case Measurement:
		getDeviceEventManagement().addDeviceMeasurements(assignmentId,
			(IDeviceMeasurementCreateRequest) request);
		break;
	    case Alert:
		getDeviceEventManagement().addDeviceAlerts(assignmentId, (IDeviceAlertCreateRequest) request);
		break;
	    case CommandInvocation:
		getDeviceEventManagement().addDeviceCommandInvocations(assignmentId,
			(IDeviceCommandInvocationCreateRequest) request);
		break;
	    case CommandResponse:
		getDeviceEventManagement().addDeviceCommandResponses(assignmentId,
			(IDeviceCommandResponseCreateRequest) request);
		break;
	    case Location:
		getDeviceEventManagement().addDeviceLocations(assignmentId, (IDeviceLocationCreateRequest) request);
		break;
	    case StateChange:
		getDeviceEventManagement().addDeviceStateChanges(assignmentId,
			(IDeviceStateChangeCreateRequest) request);
		break;
	    default:
		getLogger()
			.warn(String.format("Unknown event type sent for storage: %s", request.getEventType().name()));
	    }

	    // Keep metrics on processed events.
	    getProcessedEvents().labels(buildLabels()).inc();
	}
    }

    protected Gauge getKafkaBatchSize() {
	return kafkaBatchSize;
    }

    protected Counter getProcessedEvents() {
	return processedEvents;
    }

    protected IDeviceEventManagement getDeviceEventManagement() {
	return ((IEventManagementTenantEngine) getTenantEngine()).getEventManagement();
    }
}
