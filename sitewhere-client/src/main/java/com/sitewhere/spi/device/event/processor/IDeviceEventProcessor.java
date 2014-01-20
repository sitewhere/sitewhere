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

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;

/**
 * Allows external systems to interact with SiteWhere event processing.
 * 
 * @author Derek
 */
public interface IDeviceEventProcessor {

	/**
	 * Start the processor.
	 * 
	 * @throws SiteWhereException
	 */
	public void start() throws SiteWhereException;

	/**
	 * Stop the processor.
	 * 
	 * @throws SiteWhereException
	 */
	public void stop() throws SiteWhereException;

	/**
	 * Executes code before saving device measurements.
	 * 
	 * @param assignment
	 * @param request
	 * @throws SiteWhereException
	 */
	public void beforeMeasurements(IDeviceAssignment assignment, IDeviceMeasurementsCreateRequest request)
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
	public void beforeLocation(IDeviceAssignment assignment, IDeviceLocationCreateRequest request)
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
	public void beforeAlert(IDeviceAssignment assignment, IDeviceAlertCreateRequest request)
			throws SiteWhereException;

	/**
	 * Executes code after device alert has been successfully saved.
	 * 
	 * @param location
	 * @throws SiteWhereException
	 */
	public void afterAlert(IDeviceAlert alert) throws SiteWhereException;
}