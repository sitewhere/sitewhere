/*
 * IZoneRelationship.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.geo;

import com.sitewhere.spi.device.IDeviceLocation;
import com.sitewhere.spi.device.IZone;

/**
 * Represents a relationship between a location and a zone.
 * 
 * @author Derek
 */
public interface IZoneRelationship {

	/**
	 * Get device location.
	 * 
	 * @return
	 */
	public IDeviceLocation getLocation();

	/**
	 * Get zone.
	 * 
	 * @return
	 */
	public IZone getZone();

	/**
	 * Get containment indicator.
	 * 
	 * @return
	 */
	public ZoneContainment getContainment();
}