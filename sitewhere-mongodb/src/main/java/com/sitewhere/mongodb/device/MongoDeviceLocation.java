/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.device;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.spi.device.event.IDeviceLocation;

/**
 * Used to load or save device location data to MongoDB.
 * 
 * @author dadams
 */
public class MongoDeviceLocation implements MongoConverter<IDeviceLocation> {

    /** Element that holds location information */
    public static final String PROP_LATLONG = "latLong";

    /** Property for latitude */
    public static final String PROP_LATITUDE = "latitude";

    /** Property for longitude */
    public static final String PROP_LONGITUDE = "longitude";

    /** Property for elevation */
    public static final String PROP_ELEVATION = "elevation";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.dao.mongodb.MongoConverter#convert(java.lang.Object)
     */
    public BasicDBObject convert(IDeviceLocation source) {
	return MongoDeviceLocation.toDBObject(source, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.dao.mongodb.MongoConverter#convert(com.mongodb.DBObject)
     */
    public IDeviceLocation convert(DBObject source) {
	return MongoDeviceLocation.fromDBObject(source, false);
    }

    /**
     * Copy information from SPI into Mongo DBObject.
     * 
     * @param source
     * @param target
     * @param isNested
     */
    public static void toDBObject(IDeviceLocation source, BasicDBObject target, boolean isNested) {
	MongoDeviceEvent.toDBObject(source, target, isNested);

	BasicDBObject locFields = new BasicDBObject();
	locFields.append(PROP_LONGITUDE, source.getLongitude());
	locFields.append(PROP_LATITUDE, source.getLatitude());
	target.append(PROP_LATLONG, locFields);
	if (source.getElevation() != null) {
	    target.append(PROP_ELEVATION, source.getElevation());
	}
    }

    /**
     * Copy information from Mongo DBObject to model object.
     * 
     * @param source
     * @param target
     * @param isNested
     */
    public static void fromDBObject(DBObject source, DeviceLocation target, boolean isNested) {
	MongoDeviceEvent.fromDBObject(source, target, isNested);

	DBObject location = (DBObject) source.get(PROP_LATLONG);
	Double latitude = (Double) location.get(PROP_LATITUDE);
	Double longitude = (Double) location.get(PROP_LONGITUDE);
	Double elevation = (Double) source.get(PROP_ELEVATION);

	target.setLatitude(latitude);
	target.setLongitude(longitude);
	target.setElevation(elevation);
    }

    /**
     * Convert SPI object to Mongo DBObject.
     * 
     * @param source
     * @param isNested
     * @return
     */
    public static BasicDBObject toDBObject(IDeviceLocation source, boolean isNested) {
	BasicDBObject result = new BasicDBObject();
	MongoDeviceLocation.toDBObject(source, result, isNested);
	return result;
    }

    /**
     * Convert a DBObject into the SPI equivalent.
     * 
     * @param source
     * @param isNested
     * @return
     */
    public static DeviceLocation fromDBObject(DBObject source, boolean isNested) {
	DeviceLocation result = new DeviceLocation();
	MongoDeviceLocation.fromDBObject(source, result, isNested);
	return result;
    }
}
