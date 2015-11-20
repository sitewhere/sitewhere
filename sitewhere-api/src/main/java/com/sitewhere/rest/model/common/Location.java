/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.common;

import com.sitewhere.spi.common.ILocation;

/**
 * Model object for a location in 3d space.
 * 
 * @author dadams
 */
public class Location implements ILocation {

	/** Serial version UID */
	private static final long serialVersionUID = 7247443984993993837L;

	/** Latitude measurement */
	private Double latitude;

	/** Longitude measurement */
	private Double longitude;

	/** Elevation measurement */
	private Double elevation;

	public Location() {
	}

	public Location(Double latitude, Double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = 0.0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.common.ILocation#getLatitude()
	 */
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.common.ILocation#getLongitude()
	 */
	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.common.ILocation#getElevation()
	 */
	public Double getElevation() {
		return elevation;
	}

	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}

	/**
	 * Create a copy of an SPI object. Used by web services for marshaling.
	 * 
	 * @param input
	 * @return
	 */
	public static Location copy(ILocation input) {
		Location result = new Location();
		result.setLatitude(input.getLatitude());
		result.setLongitude(input.getLongitude());
		result.setElevation(input.getElevation());
		return result;
	}
}