/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset.request;

import com.sitewhere.spi.asset.ILocationAsset;
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
     * @see
     * com.sitewhere.spi.asset.request.ILocationAssetCreateRequest#getLatitude()
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
     * @see
     * com.sitewhere.spi.asset.request.ILocationAssetCreateRequest#getLongitude(
     * )
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
     * @see
     * com.sitewhere.spi.asset.request.ILocationAssetCreateRequest#getElevation(
     * )
     */
    public Double getElevation() {
	return elevation;
    }

    public void setElevation(Double elevation) {
	this.elevation = elevation;
    }

    public static class Builder {

	/** Request being built */
	private LocationAssetCreateRequest request = new LocationAssetCreateRequest();

	public Builder(ILocationAsset asset) {
	    this(asset.getId(), asset.getName(), asset.getImageUrl());
	    request.setLatitude(asset.getLatitude());
	    request.setLongitude(asset.getLongitude());
	    request.setElevation(asset.getElevation());
	    request.getProperties().putAll(asset.getProperties());
	}

	public Builder(String id, String name, String imageUrl) {
	    request.setId(id);
	    request.setName(name);
	    request.setImageUrl(imageUrl);
	}

	public Builder withLatitude(double latitude) {
	    request.setLatitude(latitude);
	    return this;
	}

	public Builder withLongitude(double longitude) {
	    request.setLongitude(longitude);
	    return this;
	}

	public Builder withElevation(double elevation) {
	    request.setElevation(elevation);
	    return this;
	}

	public LocationAssetCreateRequest build() {
	    return request;
	}
    }
}