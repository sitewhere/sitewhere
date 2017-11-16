/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.mongodb;

import org.bson.Document;

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
    public static final String PROP_LATLONG = "ll";

    /** Property for latitude */
    public static final String PROP_LATITUDE = "lt";

    /** Property for longitude */
    public static final String PROP_LONGITUDE = "ln";

    /** Property for elevation */
    public static final String PROP_ELEVATION = "el";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.dao.mongodb.MongoConverter#convert(java.lang.Object)
     */
    public Document convert(IDeviceLocation source) {
	return MongoDeviceLocation.toDocument(source, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    public IDeviceLocation convert(Document source) {
	return MongoDeviceLocation.fromDocument(source, false);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     * @param isNested
     */
    public static void toDocument(IDeviceLocation source, Document target, boolean isNested) {
	MongoDeviceEvent.toDocument(source, target, isNested);

	Document locFields = new Document();
	locFields.append(PROP_LONGITUDE, source.getLongitude());
	locFields.append(PROP_LATITUDE, source.getLatitude());
	target.append(PROP_LATLONG, locFields);
	if (source.getElevation() != null) {
	    target.append(PROP_ELEVATION, source.getElevation());
	}
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     * @param isNested
     */
    public static void fromDocument(Document source, DeviceLocation target, boolean isNested) {
	MongoDeviceEvent.fromDocument(source, target, isNested);

	Document location = (Document) source.get(PROP_LATLONG);
	Double latitude = (Double) location.get(PROP_LATITUDE);
	Double longitude = (Double) location.get(PROP_LONGITUDE);
	Double elevation = (Double) source.get(PROP_ELEVATION);

	target.setLatitude(latitude);
	target.setLongitude(longitude);
	target.setElevation(elevation);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @param isNested
     * @return
     */
    public static Document toDocument(IDeviceLocation source, boolean isNested) {
	Document result = new Document();
	MongoDeviceLocation.toDocument(source, result, isNested);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @param isNested
     * @return
     */
    public static DeviceLocation fromDocument(Document source, boolean isNested) {
	DeviceLocation result = new DeviceLocation();
	MongoDeviceLocation.fromDocument(source, result, isNested);
	return result;
    }
}
