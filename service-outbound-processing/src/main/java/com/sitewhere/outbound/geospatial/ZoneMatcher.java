/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.outbound.geospatial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.geospatial.IZoneMatcher;
import com.sitewhere.spi.geospatial.IZoneRelationship;
import com.sitewhere.spi.geospatial.ZoneContainment;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Matches a list of zones against a location and stores the results.
 * 
 * @author Derek
 */
public class ZoneMatcher implements IZoneMatcher {

    /** Device location */
    private IDeviceLocation location;

    /** Relationships by zone id */
    private Map<String, IZoneRelationship> relationships = new HashMap<String, IZoneRelationship>();

    public <T extends IZone> ZoneMatcher(IDeviceLocation location, List<T> zones) {
	this.location = location;
	for (IZone zone : zones) {
	    Polygon zonePoly = GeoUtils.createPolygonForZone(zone);
	    ZoneContainment containment = (zonePoly.contains(GeoUtils.createPointForLocation(location)))
		    ? ZoneContainment.Inside : ZoneContainment.Outside;
	    ZoneRelationship relationship = new ZoneRelationship(location, zone, containment);
	    relationships.put(zone.getToken(), relationship);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.geo.IZoneMatcher#getLocation()
     */
    public IDeviceLocation getLocation() {
	return location;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.geo.IZoneMatcher#getRelationships()
     */
    public Map<String, IZoneRelationship> getRelationships() {
	return relationships;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.geo.IZoneMatcher#getRelationship(java.lang.String)
     */
    public IZoneRelationship getRelationship(String zoneId) {
	return relationships.get(zoneId);
    }
}