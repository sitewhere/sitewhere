/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication;

import java.util.Map;

import org.apache.log4j.Logger;

import com.sitewhere.spi.device.communication.EventDecodeException;
import com.sitewhere.spi.device.communication.IInboundEventReceiver;

/**
 * Handlers for event processing logic. These methods will eventually be refactored into
 * framework handlers.
 * 
 * @author Derek
 */
public class EventProcessingLogic {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(EventProcessingLogic.class);

	/**
	 * Common processing logic for delivering a raw payload to event receiver to be
	 * decoded. This version logs an error if decoding fails.
	 * 
	 * @param receiver
	 * @param payload
	 * @param metadata
	 */
	public static <T> void processRawPayload(IInboundEventReceiver<T> receiver, T payload,
			Map<String, String> metadata) {
		try {
			receiver.onEventPayloadReceived(payload, metadata);
		} catch (EventDecodeException e) {
			LOGGER.error("Unable to decode event payload.", e);
		}
	}

	/**
	 * Common processing logic for delivering a raw payload to event receiver to be
	 * decoded. This version bubbles decoder exceptions to allow handling in the caller.
	 * 
	 * @param receiver
	 * @param payload
	 * @param metadata
	 * @throws EventDecodeException
	 */
	public static <T> void processRawPayloadWithExceptionHandling(IInboundEventReceiver<T> receiver,
			T payload, Map<String, String> metadata) throws EventDecodeException {
		receiver.onEventPayloadReceived(payload, metadata);
	}
}