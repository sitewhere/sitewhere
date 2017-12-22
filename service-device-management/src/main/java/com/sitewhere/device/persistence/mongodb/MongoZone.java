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
import java.util.UUID;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.common.Location;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.spi.common.ILocation;
import com.sitewhere.spi.device.IZone;

/**
 * Used to load or save zone data to MongoDB.
 * 
 * @author dadams
 */
public class MongoZone implements MongoConverter<IZone> {

    /** Property for id */
    public static final String PROP_ID = "id";

    /** Property for unique token */
    public static final String PROP_TOKEN = "tk";

    /** Property for site id */
    public static final String PROP_SITE_ID = "si";

    /** Property for name */
    public static final String PROP_NAME = "nm";

    /** Property for border color */
    public static final String PROP_BORDER_COLOR = "bc";

    /** Property for fill color */
    public static final String PROP_FILL_COLOR = "fc";

    /** Property for opacity */
    public static final String PROP_OPACITY = "op";

    /** Property for coordinates */
    public static final String PROP_COORDINATES = "co";

    /** Property for latitude */
    public static final String PROP_LATITUDE = "la";

    /** Property for longitude */
    public static final String PROP_LONGITUDE = "ln";

    /** Property for elevation */
    public static final String PROP_ELEVATION = "el";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.dao.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IZone source) {
	return MongoZone.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IZone convert(Document source) {
	return MongoZone.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IZone source, Document target) {
	target.append(PROP_ID, source.getId());
	target.append(PROP_TOKEN, source.getToken());
	target.append(PROP_SITE_ID, source.getSiteId());
	target.append(PROP_NAME, source.getName());
	target.append(PROP_BORDER_COLOR, source.getBorderColor());
	target.append(PROP_FILL_COLOR, source.getFillColor());
	target.append(PROP_OPACITY, source.getOpacity());

	ArrayList<Document> coords = new ArrayList<Document>();
	if (source.getCoordinates() != null) {
	    for (ILocation location : source.getCoordinates()) {
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

	MongoSiteWhereEntity.toDocument(source, target);
	MongoMetadataProvider.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    @SuppressWarnings("unchecked")
    public static void fromDocument(Document source, Zone target) {
	UUID id = (UUID) source.get(PROP_ID);
	String token = (String) source.get(PROP_TOKEN);
	UUID siteId = (UUID) source.get(PROP_SITE_ID);
	String name = (String) source.get(PROP_NAME);
	String borderColor = (String) source.get(PROP_BORDER_COLOR);
	String fillColor = (String) source.get(PROP_FILL_COLOR);
	Double opacity = (Double) source.get(PROP_OPACITY);

	target.setId(id);
	target.setToken(token);
	target.setSiteId(siteId);
	target.setName(name);
	target.setBorderColor(borderColor);
	target.setFillColor(fillColor);
	target.setOpacity(opacity);

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
	target.setCoordinates(locs);

	MongoSiteWhereEntity.fromDocument(source, target);
	MongoMetadataProvider.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IZone source) {
	Document result = new Document();
	MongoZone.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static Zone fromDocument(Document source) {
	Zone result = new Zone();
	MongoZone.fromDocument(source, result);
	return result;
    }
}