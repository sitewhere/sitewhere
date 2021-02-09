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
     * @param event
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