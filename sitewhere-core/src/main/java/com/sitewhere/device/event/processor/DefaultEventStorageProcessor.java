/*
 * DefaultEventStorageProcessor.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.event.processor;

import org.apache.log4j.Logger;

import com.sitewhere.rest.model.device.event.processor.InboundEventProcessor;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessor;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;

/**
 * Implementation of {@link IInboundEventProcessor} that attempts to store the inbound
 * event request using device management APIs.
 * 
 * @author Derek
 */
public class DefaultEventStorageProcessor extends InboundEventProcessor {

	/** Static logger instance */
	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(DefaultEventStorageProcessor.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IInboundEventProcessor#onRegistrationRequest
	 * (com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest)
	 */
	@Override
	public void onRegistrationRequest(IDeviceRegistrationRequest request) throws SiteWhereException {
		SiteWhereServer.getInstance().getDeviceProvisioning().getRegistrationManager().handleDeviceRegistration(
				request);
	}
}