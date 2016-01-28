/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Manages concurrent updates to assignment state to prevent thrashing as the result of
 * high event throughput with state updates.
 * 
 * @author Derek
 */
public interface IAssignmentStateManager extends ITenantLifecycleComponent {
	/**
	 * Add new location state for an assignment.
	 * 
	 * @param token
	 * @param location
	 * @throws SiteWhereException
	 */
	public void addLocation(String token, IDeviceLocation location) throws SiteWhereException;

	/**
	 * Add new measurements state for an assignment.
	 * 
	 * @param token
	 * @param measurements
	 * @throws SiteWhereException
	 */
	public void addMeasurements(String token, IDeviceMeasurements measurements) throws SiteWhereException;

	/**
	 * Add new alert state for an assignment.
	 * 
	 * @param token
	 * @param alert
	 * @throws SiteWhereException
	 */
	public void addAlert(String token, IDeviceAlert alert) throws SiteWhereException;
}