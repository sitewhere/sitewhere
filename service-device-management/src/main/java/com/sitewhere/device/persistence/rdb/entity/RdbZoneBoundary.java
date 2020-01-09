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

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "zone_boundary")
public class RdbZoneBoundary extends RdbLocation {

    /** Serial version UID */
    private static final long serialVersionUID = -1861803961883686395L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "zone_id", nullable = false)
    private UUID zoneId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zone_id", insertable = false, updatable = false)
    private RdbZone zone;

    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    public UUID getZoneId() {
	return zoneId;
    }

    public void setZoneId(UUID zoneId) {
	this.zoneId = zoneId;
    }

    public RdbZone getZone() {
	return zone;
    }

    public void setZone(RdbZone zone) {
	this.zone = zone;
    }
}
