/*
* $Id$
* --------------------------------------------------------------------------------------
* Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
*
* The software in this package is published under the terms of the CPAL v1.0
* license, a copy of which has been included with this distribution in the
* LICENSE.txt file.
*/

package com.sitewhere.spi.device;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;

/**
 * Delegate that handles storage of data related to device state.
 * 
 * @author dadams
 */
public interface IDeviceStateStorageDelegate {

	/**
	 * Add measurements for a given device assignment.
	 * 
	 * @param measurements
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceMeasurements addDeviceMeasurements(IDeviceMeasurements measurements)
			throws SiteWhereException;

	/**
	 * Add a location for a given device assignment.
	 * 
	 * @param location
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceLocation addDeviceLocation(IDeviceLocation location) throws SiteWhereException;

	/**
	 * Add an alert for a given device assignment.
	 * 
	 * @param alert
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceAlert addDeviceAlert(IDeviceAlert alert) throws SiteWhereException;

	/**
	 * Adds an alert which is associated with a given location.
	 * 
	 * @param locationId
	 * @param alert
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceAlert addAlertForLocation(String locationId, IDeviceAlert alert) throws SiteWhereException;

	/**
	 * Adds an alert which is associated with a given set of measurements.
	 * 
	 * @param measurementsId
	 * @param alert
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceAlert addAlertForMeasurements(String measurementsId, IDeviceAlert alert)
			throws SiteWhereException;

	/**
	 * Gets most recent device measurements associated with a device assignment.
	 * 
	 * @param assignmentToken
	 * @param maxCount
	 * @return
	 * @throws SiteWhereException
	 */
	public List<IDeviceMeasurements> listDeviceMeasurements(String assignmentToken, int maxCount)
			throws SiteWhereException;

	/**
	 * Gets the most recent device locations associated with a device assignment.
	 * 
	 * @param assignmentToken
	 * @param maxCount
	 * @return
	 * @throws SiteWhereException
	 */
	public List<IDeviceLocation> listDeviceLocations(String assignmentToken, int maxCount)
			throws SiteWhereException;

	/**
	 * Gets the most recent alerts associated with a device assignment.
	 * 
	 * @param assignmentToken
	 * @param maxCount
	 * @return
	 * @throws SiteWhereException
	 */
	public List<IDeviceAlert> listDeviceAlerts(String assignmentToken, int maxCount)
			throws SiteWhereException;
}