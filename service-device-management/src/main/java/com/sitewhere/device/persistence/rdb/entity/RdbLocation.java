/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
