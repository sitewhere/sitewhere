/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.request;

import java.util.Map;

import com.sitewhere.spi.device.event.state.StateChangeCategory;
import com.sitewhere.spi.device.event.state.StateChangeType;

/**
 * Request from a device to update its state in SiteWhere.
 * 
 * @author Derek
 */
public interface IDeviceStateChangeCreateRequest extends IDeviceEventCreateRequest {

	/**
	 * Get category of state change.
	 * 
	 * @return
	 */
	public StateChangeCategory getCategory();

	/**
	 * Get type of state change.
	 * 
	 * @return
	 */
	public StateChangeType getType();

	/**
	 * Get the previous (or assumed previous) state.
	 * 
	 * @return
	 */
	public String getPreviousState();

	/**
	 * Get the requested new state.
	 * 
	 * @return
	 */
	public String getNewState();

	/**
	 * Get data associated with the state change.
	 * 
	 * @return
	 */
	public Map<String, String> getData();
}