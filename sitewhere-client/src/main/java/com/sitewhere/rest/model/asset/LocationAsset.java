/*
 * LocationAsset.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset;

import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.ILocationAsset;

/**
 * Default implementation of {@link ILocationAsset}.
 * 
 * @author Derek
 */
public class LocationAsset extends Asset implements ILocationAsset {

	/** Latitude */
	private double latitude;

	/** Longitude */
	private double longitude;

	/** Elevation */
	private double elevation;

	public LocationAsset() {
		setType(AssetType.Location);
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getElevation() {
		return elevation;
	}

	public void setElevation(double elevation) {
		this.elevation = elevation;
	}
}