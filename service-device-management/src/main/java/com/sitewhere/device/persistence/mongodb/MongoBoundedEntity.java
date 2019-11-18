/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.sitewhere.rest.model.common.Location;
import com.sitewhere.spi.area.IBoundedEntity;
import com.sitewhere.spi.common.ILocation;

/**
 * Common functionality for entities with geospatial boundaries.
 */
public class MongoBoundedEntity {

    /** Property for coordinates */
    public static final String PROP_COORDINATES = "coor";

    /** Property for latitude */
    public static final String PROP_LATITUDE = "lati";

    /** Property for longitude */
    public static final String PROP_LONGITUDE = "long";

    /** Property for elevation */
    public static final String PROP_ELEVATION = "elev";

    /**
     * Save coordinates to a document.
     * 
     * @param source
     * @param target
     */
    public static void saveBounds(IBoundedEntity source, Document target) {
	ArrayList<Document> coords = new ArrayList<Document>();
	if (source.getBounds() != null) {
	    for (ILocation location : source.getBounds()) {
		Document coord = new Document();
		coord.put(PROP_LATITUDE, location.getLatitude());
		coord.put(PROP_LONGITUDE, location.getLongitude());
		if (location.getElevation() != null) {
		    coord.put(PROP_ELEVATION, location.getElevation());
		}
		coords.add(coord);
	    }
	}
	target.append(PROP_COORDINATES, coords);
    }

    /**
     * Load coordinates from a document.
     * 
     * @param source
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Location> loadBounds(Document source) {
	List<Location> locs = new ArrayList<Location>();
	ArrayList<Document> coords = (ArrayList<Document>) source.get(PROP_COORDINATES);
	for (int i = 0; i < coords.size(); i++) {
	    Document coord = coords.get(i);
	    Location loc = new Location();
	    loc.setLatitude((Double) coord.get(PROP_LATITUDE));
	    loc.setLongitude((Double) coord.get(PROP_LONGITUDE));
	    loc.setElevation((Double) coord.get(PROP_ELEVATION));
	    locs.add(loc);
	}
	return locs;
    }
}