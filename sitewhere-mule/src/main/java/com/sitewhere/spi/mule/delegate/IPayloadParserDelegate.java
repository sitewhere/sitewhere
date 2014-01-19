/*
 * IPayloadParserDelegate.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.mule.delegate;

import java.util.List;

import org.mule.api.MuleEvent;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;

/**
 * Interface for class that parses SiteWhere context information from a given type of payload.
 * 
 * @author Derek
 */
public interface IPayloadParserDelegate {

	/**
	 * Initialize the delegate with information from the current MuleEvent.
	 * 
	 * @param event
	 * @throws SiteWhereException
	 */
	public void initialize(MuleEvent event) throws SiteWhereException;

	/**
	 * Get hardware id of the device the payload references.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public String getDeviceHardwareId() throws SiteWhereException;

	/**
	 * Get information for replying to the originator.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public String getReplyTo() throws SiteWhereException;

	/**
	 * Get a list of location create requests associated with the payload.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public List<IDeviceLocationCreateRequest> getLocations() throws SiteWhereException;

	/**
	 * Get a list of measurement create requests associated with the payload.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public List<IDeviceMeasurementsCreateRequest> getMeasurements() throws SiteWhereException;

	/**
	 * Get a list of alert create requests associated with the payload.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public List<IDeviceAlertCreateRequest> getAlerts() throws SiteWhereException;
}