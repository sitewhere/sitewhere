/*
 * IDeviceStateChange.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

import java.util.Map;

import com.sitewhere.spi.device.event.state.StateChangeCategory;
import com.sitewhere.spi.device.event.state.StateChangeType;

/**
 * Event that captures a change of state (either requested or after the fact) for a
 * device.
 * 
 * @author Derek
 */
public interface IDeviceStateChange extends IDeviceEvent {

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