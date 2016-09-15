/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.device;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
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

    /** Property for unique token */
    public static final String PROP_TOKEN = "token";

    /** Property for site token */
    public static final String PROP_SITE_TOKEN = "siteToken";

    /** Property for name */
    public static final String PROP_NAME = "name";

    /** Property for border color */
    public static final String PROP_BORDER_COLOR = "borderColor";

    /** Property for fill color */
    public static final String PROP_FILL_COLOR = "fillColor";

    /** Property for opacity */
    public static final String PROP_OPACITY = "opacity";

    /** Property for coordinates */
    public static final String PROP_COORDINATES = "coordinates";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.dao.mongodb.MongoConverter#convert(java.lang.Object)
     */
    public BasicDBObject convert(IZone source) {
	return MongoZone.toDBObject(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.dao.mongodb.MongoConverter#convert(com.mongodb.DBObject)
     */
    public IZone convert(DBObject source) {
	return MongoZone.fromDBObject(source);
    }

    /**
     * Copy information from SPI into Mongo DBObject.
     * 
     * @param source
     * @param target
     */
    public static void toDBObject(IZone source, BasicDBObject target) {
	target.append(PROP_TOKEN, source.getToken());
	target.append(PROP_SITE_TOKEN, source.getSiteToken());
	target.append(PROP_NAME, source.getName());
	target.append(PROP_BORDER_COLOR, source.getBorderColor());
	target.append(PROP_FILL_COLOR, source.getFillColor());
	target.append(PROP_OPACITY, source.getOpacity());

	BasicDBList coords = new BasicDBList();
	if (source.getCoordinates() != null) {
	    for (ILocation location : source.getCoordinates()) {
		BasicDBObject coord = new BasicDBObject();
		coord.put(MongoDeviceLocation.PROP_LATITUDE, location.getLatitude());
		coord.put(MongoDeviceLocation.PROP_LONGITUDE, location.getLongitude());
		if (location.getElevation() != null) {
		    coord.put(MongoDeviceLocation.PROP_ELEVATION, location.getElevation());
		}
		coords.add(coord);
	    }
	}
	target.append(PROP_COORDINATES, coords);

	MongoSiteWhereEntity.toDBObject(source, target);
	MongoMetadataProvider.toDBObject(source, target);
    }

    /**
     * Copy information from Mongo DBObject to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDBObject(DBObject source, Zone target) {
	String token = (String) source.get(PROP_TOKEN);
	String siteToken = (String) source.get(PROP_SITE_TOKEN);
	String name = (String) source.get(PROP_NAME);
	String borderColor = (String) source.get(PROP_BORDER_COLOR);
	String fillColor = (String) source.get(PROP_FILL_COLOR);
	Double opacity = (Double) source.get(PROP_OPACITY);

	target.setToken(token);
	target.setSiteToken(siteToken);
	target.setName(name);
	target.setBorderColor(borderColor);
	target.setFillColor(fillColor);
	target.setOpacity(opacity);

	List<Location> locs = new ArrayList<Location>();
	BasicDBList coords = (BasicDBList) source.get(PROP_COORDINATES);
	for (int i = 0; i < coords.size(); i++) {
	    DBObject coord = (DBObject) coords.get(i);
	    Location loc = new Location();
	    loc.setLatitude((Double) coord.get(MongoDeviceLocation.PROP_LATITUDE));
	    loc.setLongitude((Double) coord.get(MongoDeviceLocation.PROP_LONGITUDE));
	    loc.setElevation((Double) coord.get(MongoDeviceLocation.PROP_ELEVATION));
	    locs.add(loc);
	}
	target.setCoordinates(locs);

	MongoSiteWhereEntity.fromDBObject(source, target);
	MongoMetadataProvider.fromDBObject(source, target);
    }

    /**
     * Convert SPI object to Mongo DBObject.
     * 
     * @param source
     * @return
     */
    public static BasicDBObject toDBObject(IZone source) {
	BasicDBObject result = new BasicDBObject();
	MongoZone.toDBObject(source, result);
	return result;
    }

    /**
     * Convert a DBObject into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static Zone fromDBObject(DBObject source) {
	Zone result = new Zone();
	MongoZone.fromDBObject(source, result);
	return result;
    }
}