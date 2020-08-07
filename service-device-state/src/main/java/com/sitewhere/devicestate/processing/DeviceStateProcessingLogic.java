/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.processing;

import java.util.Date;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine;
import com.sitewhere.devicestate.spi.processing.IDeviceStateProcessingLogic;
import com.sitewhere.grpc.event.EventModelConverter;
import com.sitewhere.grpc.event.EventModelMarshaler;
import com.sitewhere.grpc.model.DeviceEventModel.GProcessedEventPayload;
import com.sitewhere.microservice.api.state.IDeviceStateManagement;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.rest.model.device.event.kafka.ProcessedEventPayload;
import com.sitewhere.rest.model.device.state.request.DeviceStateCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.state.IDeviceState;

import io.prometheus.client.Counter;

/**
 * Processing logic applied to enriched inbound event payloads in order to
 * capture device state.
 */
public class DeviceStateProcessingLogic extends TenantEngineLifecycleComponent implements IDeviceStateProcessingLogic {

    /** Counter for processed events */
    private static final Counter PROCESSED_EVENTS = TenantEngineLifecycleComponent
	    .createCounterMetric("processed_event_count", "Count of total events processed by consumer");

    /*
     * @see
     * com.sitewhere.devicestate.spi.processing.IDeviceStateProcessingLogic#process(
     * java.util.List)
     */
    @Override
    public void process(List<ConsumerRecord<String, byte[]>> records) throws SiteWhereException {
	processPayloads(records);
    }

    /**
     * Build requests based on batch of Kafka records.
     * 
     * @param records
     * @return
     */
    protected void processPayloads(List<ConsumerRecord<String, byte[]>> records) throws SiteWhereException {
	for (ConsumerRecord<String, byte[]> record : records) {
	    try {
		processRecord(record);
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to process event for device state.", e);
	    } catch (Throwable e) {
		getLogger().error("Unhandled exception while processing event for device state.", e);
	    }
	}
    }

    /**
     * Process a single record.
     * 
     * @param record
     * @throws SiteWhereException
     */
    protected void processRecord(ConsumerRecord<String, byte[]> record) throws SiteWhereException {
	PROCESSED_EVENTS.labels(buildLabels()).inc();
	try {
	    GProcessedEventPayload grpc = EventModelMarshaler.parseProcessedEventPayloadMessage(record.value());
	    ProcessedEventPayload payload = EventModelConverter.asApiProcessedEventPayload(grpc);
	    if (getLogger().isDebugEnabled()) {
		getLogger().debug(
			"Received enriched event payload:\n\n" + MarshalUtils.marshalJsonAsPrettyString(payload));
	    }
	    processDeviceStateEvent(payload);
	} catch (SiteWhereException e) {
	    getLogger().error("Unable to process outbound connector event payload.", e);
	} catch (Throwable e) {
	    getLogger().error("Unhandled exception processing connector event payload.", e);
	}
    }

    /**
     * Process a single enriched event to capture device state.
     * 
     * @param payload
     * @throws SiteWhereException
     */
    @SuppressWarnings("unused")
    protected void processDeviceStateEvent(ProcessedEventPayload payload) throws SiteWhereException {
	// Only process events that affect state.
	IDeviceEvent event = payload.getEvent();
	IDeviceEventContext context = payload.getEventContext();
	// IDeviceState original = getDeviceStateManagement()
	// .getDeviceStateByDeviceAssignmentId(event.getDeviceAssignmentId());
	IDeviceState original = null;
	switch (event.getEventType()) {
	case Alert:
	case Location:
	case Measurement: {
	    break;
	}
	default: {
	    // Allow other events to trigger presence detected.
	    if ((original == null) || (original.getPresenceMissingDate() == null)) {
		return;
	    }
	}
	}
	DeviceStateCreateRequest request = new DeviceStateCreateRequest();
	request.setDeviceId(event.getDeviceId());
	request.setDeviceTypeId(context.getDeviceTypeId());
	request.setDeviceAssignmentId(event.getDeviceAssignmentId());
	request.setCustomerId(event.getCustomerId());
	request.setAreaId(event.getAreaId());
	request.setAssetId(event.getAssetId());
	request.setLastInteractionDate(new Date());
	request.setPresenceMissingDate(null);

	// Create or update device state.
	if (original != null) {
	    getDeviceStateManagement().updateDeviceState(original.getId(), request);
	} else {
	    getDeviceStateManagement().createDeviceState(request);
	}
    }

    protected IDeviceStateManagement getDeviceStateManagement() {
	return ((IDeviceStateTenantEngine) getTenantEngine()).getDeviceStateManagement();
    }
}