/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.rdb.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.sitewhere.spi.common.ILocation;

@MappedSuperclass
public class RdbLocation implements ILocation {

    /** Serial version UID */
    private static final long serialVersionUID = 6990895156056876463L;

    /** Latitude value */
    @Column(name = "latitude")
    private Double latitude;

    /** Longitude value */
    @Column(name = "longitude")
    private Double longitude;

    /** Elevation value */
    @Column(name = "elevation")
    private Double elevation;

    /**
     * Constructor
     */
    public RdbLocation() {
    }

    /**
     * Constructor
     *
     * @param latitude
     * @param longitude
     * @param elevation
     */
    public RdbLocation(Double latitude, Double longitude, Double elevation) {
	this.latitude = latitude;
	this.longitude = longitude;
	this.elevation = elevation;
    }

    /*
     * @see com.sitewhere.spi.common.ILocation#getLatitude()
     */
    @Override
    public Double getLatitude() {
	return latitude;
    }

    public void setLatitude(Double latitude) {
	this.latitude = latitude;
    }

    /*
     * @see com.sitewhere.spi.common.ILocation#getLongitude()
     */
    @Override
    public Double getLongitude() {
	return longitude;
    }

    public void setLongitude(Double longitude) {
	this.longitude = longitude;
    }

    /*
     * @see com.sitewhere.spi.common.ILocation#getElevation()
     */
    @Override
    public Double getElevation() {
	return elevation;
    }

    public void setElevation(Double elevation) {
	this.elevation = elevation;
    }

    public static void copy(ILocation source, RdbLocation target) {
	if (source.getElevation() != null) {
	    target.setElevation(source.getElevation());
	}
	if (source.getLatitude() != null) {
	    target.setLatitude(source.getLatitude());
	}
	if (source.getLongitude() != null) {
	    target.setLongitude(source.getLongitude());
	}
    }
}
