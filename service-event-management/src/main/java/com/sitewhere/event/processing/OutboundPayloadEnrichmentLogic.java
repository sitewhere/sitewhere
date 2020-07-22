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

import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.event.EventModelConverter;
import com.sitewhere.grpc.event.EventModelMarshaler;
import com.sitewhere.grpc.model.DeviceEventModel.GProcessedEventPayload;
import com.sitewhere.rest.model.device.event.kafka.ProcessedEventPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;

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
     * @param payload
     * @throws SiteWhereException
     */
    public static void enrichAndDeliver(IEventManagementTenantEngine engine, IDeviceEventContext context,
	    IDeviceEvent event) throws SiteWhereException {
	try {
	    // Build processed payload.
	    ProcessedEventPayload enriched = new ProcessedEventPayload();
	    enriched.setEventContext(context);
	    enriched.setEvent(event);

	    // Send enriched payload to topic.
	    GProcessedEventPayload grpc = EventModelConverter.asGrpcProcessedEventPayload(enriched);
	    byte[] message = EventModelMarshaler.buildProcessedEventPayloadMessage(grpc);
	    engine.getOutboundEventsProducer().send(context.getDeviceId(), message);
	    LOGGER.debug("Delivered payload to outbound events producer.");

	    // Send enriched command invocations to topic.
	    if (event.getEventType() == DeviceEventType.CommandInvocation) {
		engine.getOutboundCommandInvocationsProducer().send(context.getDeviceId(), message);
		LOGGER.debug("Delivered payload to outbound command invocations producer.");
	    }
	} catch (SiteWhereException e) {
	    throw e;
	} catch (Throwable t) {
	    throw new SiteWhereException("Unhandled exception in event enrichment logic.", t);
	}
    }
}