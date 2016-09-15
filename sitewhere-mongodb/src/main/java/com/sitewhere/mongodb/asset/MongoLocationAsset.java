/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.asset;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.asset.LocationAsset;
import com.sitewhere.spi.asset.ILocationAsset;

/**
 * Used to load or save location asset data to MongoDB.
 * 
 * @author dadams
 */
public class MongoLocationAsset implements MongoConverter<ILocationAsset> {

    /** Property for latitude */
    public static final String PROP_LATITUDE = "lat";

    /** Property for longitude */
    public static final String PROP_LONGITUDE = "lon";

    /** Property for elevation */
    public static final String PROP_ELEVATION = "ele";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public BasicDBObject convert(ILocationAsset source) {
	return MongoLocationAsset.toDBObject(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
     */
    @Override
    public ILocationAsset convert(DBObject source) {
	return MongoLocationAsset.fromDBObject(source);
    }

    /**
     * Copy information from SPI into Mongo DBObject.
     * 
     * @param source
     * @param target
     */
    public static void toDBObject(ILocationAsset source, BasicDBObject target) {
	MongoAsset.toDBObject(source, target);

	target.append(PROP_LATITUDE, source.getLatitude());
	target.append(PROP_LONGITUDE, source.getLongitude());
	target.append(PROP_ELEVATION, source.getElevation());
    }

    /**
     * Copy information from Mongo DBObject to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDBObject(DBObject source, LocationAsset target) {
	MongoAsset.fromDBObject(source, target);

	Double lat = (Double) source.get(PROP_LATITUDE);
	Double lon = (Double) source.get(PROP_LONGITUDE);
	Double ele = (Double) source.get(PROP_ELEVATION);

	target.setLatitude(lat);
	target.setLongitude(lon);
	target.setElevation(ele);
    }

    /**
     * Convert SPI object to Mongo DBObject.
     * 
     * @param source
     * @return
     */
    public static BasicDBObject toDBObject(ILocationAsset source) {
	BasicDBObject result = new BasicDBObject();
	MongoLocationAsset.toDBObject(source, result);
	return result;
    }

    /**
     * Convert a DBObject into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static LocationAsset fromDBObject(DBObject source) {
	LocationAsset result = new LocationAsset();
	MongoLocationAsset.fromDBObject(source, result);
	return result;
    }
}