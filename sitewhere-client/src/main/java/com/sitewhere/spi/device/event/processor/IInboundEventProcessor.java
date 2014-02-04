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
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.device.provisioning.IDecodedDeviceEventRequest;

/**
 * Allows intereseted entities to interact with SiteWhere inbound event processing.
 * 
 * @author Derek
 */
public interface IInboundEventProcessor extends ISiteWhereLifecycle {

	/**
	 * Handles inbound {@link IDeviceEventCreateRequest} requests.
	 * 
	 * @param request
	 * @throws SiteWhereException
	 */
	public void onDecodedDeviceEventRequest(IDecodedDeviceEventRequest request) throws SiteWhereException;

	/**
	 * Called when a {@link IDeviceRegistrationRequest} is received.
	 * 
	 * @param request
	 * @throws SiteWhereException
	 */
	public void onRegistrationRequest(String hardwareId, String originator, IDeviceRegistrationRequest request)
			throws SiteWhereException;

	/**
	 * Called to request the creation of a new {@link IDeviceLocation} based on the given
	 * information.
	 * 
	 * @param hardwareId
	 * @param originator
	 * @param request
	 * @throws SiteWhereException
	 */
	public void onDeviceLocationCreateRequest(String hardwareId, String originator,
			IDeviceLocationCreateRequest request) throws SiteWhereException;
}