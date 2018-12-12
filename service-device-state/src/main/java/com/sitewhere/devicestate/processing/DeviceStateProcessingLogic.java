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

import com.codahale.metrics.Meter;
import com.sitewhere.common.MarshalUtils;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine;
import com.sitewhere.devicestate.spi.processing.IDeviceStateProcessingLogic;
import com.sitewhere.grpc.client.event.EventModelConverter;
import com.sitewhere.grpc.client.event.EventModelMarshaler;
import com.sitewhere.grpc.model.DeviceEventModel.GEnrichedEventPayload;
import com.sitewhere.rest.model.device.event.kafka.EnrichedEventPayload;
import com.sitewhere.rest.model.device.state.request.DeviceStateCreateRequest;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.state.IDeviceState;
import com.sitewhere.spi.device.state.IDeviceStateManagement;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Processing logic applied to enriched inbound event payloads in order to
 * capture device state.
 * 
 * @author Derek
 */
public class DeviceStateProcessingLogic extends TenantEngineLifecycleComponent implements IDeviceStateProcessingLogic {

    /** Meter for counting processed events */
    private Meter processedEvents;

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Set up metrics.
	this.processedEvents = createMeterMetric("processedEvents");
    }

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
	getProcessedEvents().mark();
	try {
	    GEnrichedEventPayload grpc = EventModelMarshaler.parseEnrichedEventPayloadMessage(record.value());
	    EnrichedEventPayload payload = EventModelConverter.asApiEnrichedEventPayload(grpc);
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
    protected void processDeviceStateEvent(EnrichedEventPayload payload) throws SiteWhereException {
	// Only process events that affect state.
	IDeviceEvent event = payload.getEvent();
	IDeviceEventContext context = payload.getEventContext();
	IDeviceState original = getDeviceStateManagement()
		.getDeviceStateByDeviceAssignmentId(event.getDeviceAssignmentId());
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

	// Merge alert information.
	if (event instanceof IDeviceLocation) {
	    mergeDeviceLocation((IDeviceLocation) event, original, request);
	} else if (event instanceof IDeviceAlert) {
	    mergeDeviceAlert((IDeviceAlert) event, original, request);
	} else if (event instanceof IDeviceMeasurement) {
	    mergeDeviceMeasurements((IDeviceMeasurement) event, original, request);
	}

	// Create or update device state.
	if (original != null) {
	    getDeviceStateManagement().updateDeviceState(original.getId(), request);
	} else {
	    getDeviceStateManagement().createDeviceState(request);
	}
    }

    /**
     * Merge location information.
     * 
     * @param location
     * @param original
     * @param request
     */
    protected void mergeDeviceLocation(IDeviceLocation location, IDeviceState original,
	    DeviceStateCreateRequest request) {
	request.setLastLocationEventId(location.getId());
    }

    /**
     * Merge alert information.
     * 
     * @param alert
     * @param original
     * @param request
     */
    protected void mergeDeviceAlert(IDeviceAlert alert, IDeviceState original, DeviceStateCreateRequest request) {
	if (original != null) {
	    request.getLastAlertEventIds().putAll(original.getLastAlertEventIds());
	}
	request.getLastAlertEventIds().put(alert.getType(), alert.getId());
    }

    /**
     * Merge measurement information.
     * 
     * @param mx
     * @param original
     * @param request
     */
    protected void mergeDeviceMeasurements(IDeviceMeasurement mx, IDeviceState original,
	    DeviceStateCreateRequest request) {
	if (original != null) {
	    request.getLastMeasurementEventIds().putAll(original.getLastMeasurementEventIds());
	}
	request.getLastMeasurementEventIds().put(mx.getName(), mx.getId());
    }

    protected Meter getProcessedEvents() {
	return processedEvents;
    }

    protected IDeviceStateManagement getDeviceStateManagement() {
	return ((IDeviceStateTenantEngine) getTenantEngine()).getDeviceStateManagement();
    }
}