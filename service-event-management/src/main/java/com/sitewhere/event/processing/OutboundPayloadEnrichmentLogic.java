/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.processing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.client.event.EventModelConverter;
import com.sitewhere.grpc.client.event.EventModelMarshaler;
import com.sitewhere.grpc.model.DeviceEventModel.GEnrichedEventPayload;
import com.sitewhere.rest.model.device.event.DeviceEventContext;
import com.sitewhere.rest.model.device.event.kafka.EnrichedEventPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Logic for taking a persisted event payload, enriching it with extra
 * device/assignment data, then forwarding it to a topic for further processing.
 */
public class OutboundPayloadEnrichmentLogic {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(OutboundPayloadEnrichmentLogic.class);

    /**
     * Process a persited event payload by enriching it and forwarding to a topic
     * for further processing.
     * 
     * @param event
     * @throws SiteWhereException
     */
    public static void enrichAndDeliver(IEventManagementTenantEngine engine, IDeviceEvent event)
	    throws SiteWhereException {
	try {
	    LOGGER.debug("Looking up device assignment.");
	    IEventManagementMicroservice microservice = (IEventManagementMicroservice) engine.getMicroservice();
	    IDeviceManagement deviceManagement = microservice.getCachedDeviceManagement();
	    IDeviceAssignment assignment = deviceManagement.getDeviceAssignment(event.getDeviceAssignmentId());
	    if (assignment == null) {
		// TODO: Is there a separate topic for these events?
		throw new SiteWhereException("Event references non-existent device assignment.");
	    }

	    LOGGER.debug("Looking up device.");
	    IDevice device = deviceManagement.getDevice(assignment.getDeviceId());
	    if (device == null) {
		// TODO: Is there a separate topic for these events?
		throw new SiteWhereException("Event references assignment for non-existent device.");
	    }

	    // Build event context.
	    DeviceEventContext context = new DeviceEventContext();
	    context.setDeviceId(device.getId());
	    context.setDeviceTypeId(device.getDeviceTypeId());
	    context.setParentDeviceId(device.getParentDeviceId());
	    context.setDeviceStatus(device.getStatus());
	    context.setDeviceMetadata(device.getMetadata());
	    context.setAssignmentStatus(assignment.getStatus());
	    context.setAssignmentMetadata(assignment.getMetadata());

	    // Build enriched payload.
	    EnrichedEventPayload enriched = new EnrichedEventPayload();
	    enriched.setEventContext(context);
	    enriched.setEvent(event);

	    // Send enriched payload to topic.
	    GEnrichedEventPayload grpc = EventModelConverter.asGrpcEnrichedEventPayload(enriched);
	    byte[] message = EventModelMarshaler.buildEnrichedEventPayloadMessage(grpc);
	    engine.getOutboundEventsProducer().send(device.getToken(), message);

	    // Send enriched command invocations to topic.
	    if (event.getEventType() == DeviceEventType.CommandInvocation) {
		engine.getOutboundCommandInvocationsProducer().send(device.getToken(), message);
	    }
	} catch (SiteWhereException e) {
	    throw e;
	} catch (Throwable t) {
	    throw new SiteWhereException("Unhandled exception in event enrichment logic.", t);
	}
    }
}