/*
 * IDeviceEventProcessor.java 
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
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;

/**
 * Allows intereseted entities to interact with SiteWhere outbound event processing.
 * 
 * @author Derek
 */
public interface IOutboundEventProcessor extends ISiteWhereLifecycle {

	/**
	 * Executes code before saving device measurements.
	 * 
	 * @param assignment
	 * @param request
	 * @throws SiteWhereException
	 */
	public void beforeMeasurements(String assignment, IDeviceMeasurementsCreateRequest request)
			throws SiteWhereException;

	/**
	 * Executes code after device measurements have been successfully saved.
	 * 
	 * @param measurements
	 * @throws SiteWhereException
	 */
	public void afterMeasurements(IDeviceMeasurements measurements) throws SiteWhereException;

	/**
	 * Executes code before saving device location.
	 * 
	 * @param assignment
	 * @param request
	 * @throws SiteWhereException
	 */
	public void beforeLocation(String assignment, IDeviceLocationCreateRequest request)
			throws SiteWhereException;

	/**
	 * Executes code after device location has been successfully saved.
	 * 
	 * @param location
	 * @throws SiteWhereException
	 */
	public void afterLocation(IDeviceLocation location) throws SiteWhereException;

	/**
	 * Executes code before saving device alert.
	 * 
	 * @param assignment
	 * @param request
	 * @throws SiteWhereException
	 */
	public void beforeAlert(String assignment, IDeviceAlertCreateRequest request) throws SiteWhereException;

	/**
	 * Executes code after device alert has been successfully saved.
	 * 
	 * @param location
	 * @throws SiteWhereException
	 */
	public void afterAlert(IDeviceAlert alert) throws SiteWhereException;

	/**
	 * Executes code before saving device command invocation.
	 * 
	 * @param assignment
	 * @param request
	 * @throws SiteWhereException
	 */
	public void beforeCommandInvocation(String assignment, IDeviceCommandInvocationCreateRequest request)
			throws SiteWhereException;

	/**
	 * Executes code after device command invocation has been successfully saved.
	 * 
	 * @param invocation
	 * @throws SiteWhereException
	 */
	public void afterCommandInvocation(IDeviceCommandInvocation invocation) throws SiteWhereException;

	/**
	 * Executes code before saving a device response.
	 * 
	 * @param assignment
	 * @param request
	 * @throws SiteWhereException
	 */
	public void beforeCommandResponse(String assignment, IDeviceCommandResponseCreateRequest request)
			throws SiteWhereException;

	/**
	 * Executes code after device command response has been successfully saved.
	 * 
	 * @param response
	 * @throws SiteWhereException
	 */
	public void afterCommandResponse(IDeviceCommandResponse response) throws SiteWhereException;
}