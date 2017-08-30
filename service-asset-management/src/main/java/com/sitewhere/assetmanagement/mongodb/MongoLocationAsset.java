/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.assetmanagement.mongodb;

import org.bson.Document;

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
    public Document convert(ILocationAsset source) {
	return MongoLocationAsset.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public ILocationAsset convert(Document source) {
	return MongoLocationAsset.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(ILocationAsset source, Document target) {
	MongoAsset.toDocument(source, target);

	target.append(PROP_LATITUDE, source.getLatitude());
	target.append(PROP_LONGITUDE, source.getLongitude());
	target.append(PROP_ELEVATION, source.getElevation());
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, LocationAsset target) {
	MongoAsset.fromDocument(source, target);

	Double lat = (Double) source.get(PROP_LATITUDE);
	Double lon = (Double) source.get(PROP_LONGITUDE);
	Double ele = (Double) source.get(PROP_ELEVATION);

	target.setLatitude(lat);
	target.setLongitude(lon);
	target.setElevation(ele);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(ILocationAsset source) {
	Document result = new Document();
	MongoLocationAsset.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static LocationAsset fromDocument(Document source) {
	LocationAsset result = new LocationAsset();
	MongoLocationAsset.fromDocument(source, result);
	return result;
    }
}