/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.event.processor;

import org.apache.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;

/**
 * Handles requests related to device streams.
 * 
 * @author Derek
 */
public class DeviceStreamProcessor extends InboundEventProcessor {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DeviceStreamProcessor.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IInboundEventProcessor#
	 * onDeviceStreamCreateRequest(java.lang.String, java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest)
	 */
	@Override
	public void onDeviceStreamCreateRequest(String hardwareId, String originator,
			IDeviceStreamCreateRequest request) throws SiteWhereException {
		SiteWhere.getServer().getDeviceCommunicationSubsystem().getDeviceStreamManager().handleDeviceStreamRequest(
				hardwareId, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IInboundEventProcessor#
	 * onDeviceStreamDataCreateRequest(java.lang.String, java.lang.String,
	 * java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest)
	 */
	@Override
	public void onDeviceStreamDataCreateRequest(String hardwareId, String originator,
			IDeviceStreamDataCreateRequest request) throws SiteWhereException {
		SiteWhere.getServer().getDeviceCommunicationSubsystem().getDeviceStreamManager().handleDeviceStreamDataRequest(
				hardwareId, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return LOGGER;
	}
}