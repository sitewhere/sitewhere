/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.provisioning;

import com.sitewhere.spi.ISiteWhereLifecycle;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessorChain;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;

/**
 * Provides a strategy for moving decoded events from an {@link IInboundEventSource} onto
 * the {@link IInboundEventProcessorChain}.
 * 
 * @author Derek
 */
public interface IInboundProcessingStrategy extends ISiteWhereLifecycle {

	/**
	 * Process an {@link IDeviceRegistrationRequest}.
	 * 
	 * @param request
	 * @throws SiteWhereException
	 */
	public void processRegistration(IDecodedDeviceEventRequest request) throws SiteWhereException;

	/**
	 * Process an {@link IDeviceCommandResponseCreateRequest}.
	 * 
	 * @param request
	 * @throws SiteWhereException
	 */
	public void processDeviceCommandResponse(IDecodedDeviceEventRequest request) throws SiteWhereException;

	/**
	 * Process an {@link IDeviceMeasurementsCreateRequest}.
	 * 
	 * @param request
	 * @throws SiteWhereException
	 */
	public void processDeviceMeasurements(IDecodedDeviceEventRequest request) throws SiteWhereException;

	/**
	 * Process an {@link IDeviceLocationCreateRequest}.
	 * 
	 * @param request
	 * @throws SiteWhereException
	 */
	public void processDeviceLocation(IDecodedDeviceEventRequest request) throws SiteWhereException;

	/**
	 * Process an {@link IDeviceAlertCreateRequest}.
	 * 
	 * @param request
	 * @throws SiteWhereException
	 */
	public void processDeviceAlert(IDecodedDeviceEventRequest request) throws SiteWhereException;
}