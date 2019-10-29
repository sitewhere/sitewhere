/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.entities;

import com.sitewhere.spi.common.ILocation;
import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "location")
public class Location implements ILocation {

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
    Area area;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = true)
    Zone zone;

    /**
     * Constructor
     */
    public Location() {}

    /**
     * Constructor
     *
     * @param latitude
     * @param longitude
     * @param elevation
     */
    public Location(Double latitude, Double longitude, Double elevation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    @Override
    public Double getLatitude() {
        return latitude;
    }

    @Override
    public Double getLongitude() {
        return longitude;
    }

    @Override
    public Double getElevation() {
        return elevation;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }


}
