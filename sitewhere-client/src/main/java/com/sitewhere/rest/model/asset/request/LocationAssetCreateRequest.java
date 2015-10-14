/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset.request;

import com.sitewhere.spi.asset.request.ILocationAssetCreateRequest;

/**
 * REST model implementation of {@link ILocationAssetCreateRequest}.
 * 
 * @author Derek
 */
public class LocationAssetCreateRequest extends AssetCreateRequest implements ILocationAssetCreateRequest {

	/** Serial version UID */
	private static final long serialVersionUID = 8696062232875414486L;

	/** Latitude */
	private Double latitude;

	/** Longitude */
	private Double longitude;

	/** Elevation */
	private Double elevation;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.request.ILocationAssetCreateRequest#getLatitude()
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
	 * @see com.sitewhere.spi.asset.request.ILocationAssetCreateRequest#getLongitude()
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
	 * @see com.sitewhere.spi.asset.request.ILocationAssetCreateRequest#getElevation()
	 */
	public Double getElevation() {
		return elevation;
	}

	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}
}