/*
 * IInboundEventProcessor.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.processor;

import com.sitewhere.spi.ISiteWhereLifecycle;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationCreateRequest;

/**
 * Allows intereseted entities to interact with SiteWhere inbound event processing.
 * 
 * @author Derek
 */
public interface IInboundEventProcessor extends ISiteWhereLifecycle {

	/**
	 * Called when a {@link IDeviceRegistrationCreateRequest} is received.
	 * 
	 * @param request
	 * @throws SiteWhereException
	 */
	public void onRegistrationRequest(IDeviceRegistrationCreateRequest request) throws SiteWhereException;
}