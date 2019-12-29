/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.rdb.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.sitewhere.spi.common.ILocation;

@Entity
@Table(name = "location")
public class RdbLocation implements ILocation {

    /** Serial version UID */
    private static final long serialVersionUID = 5435624345597055085L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    /** Latitude value */
    @Column(name = "latitude")
    private Double latitude;

    /** Longitude value */
    @Column(name = "longitude")
    private Double longitude;

    /** Elevation value */
    @Column(name = "elevation")
    private Double elevation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = true)
    RdbArea area;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = true)
    RdbZone zone;

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

    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
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

    public RdbArea getArea() {
	return area;
    }

    public void setArea(RdbArea area) {
	this.area = area;
    }

    public RdbZone getZone() {
	return zone;
    }

    public void setZone(RdbZone zone) {
	this.zone = zone;
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
