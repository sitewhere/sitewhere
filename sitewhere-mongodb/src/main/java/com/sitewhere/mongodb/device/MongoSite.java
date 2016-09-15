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
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.SiteMapData;
import com.sitewhere.spi.device.ISite;

/**
 * Used to load or save site data to MongoDB.
 * 
 * @author dadams
 */
public class MongoSite implements MongoConverter<ISite> {

    /** Property for name */
    public static final String PROP_NAME = "name";

    /** Property for description */
    public static final String PROP_DESCRIPTION = "description";

    /** Property for image URL */
    public static final String PROP_IMAGE_URL = "imageUrl";

    /** Property for token */
    public static final String PROP_TOKEN = "token";

    /** Property for map data */
    public static final String PROP_MAP_DATA = "mapData";

    /** Property for map type */
    public static final String PROP_MAP_TYPE = "type";

    /** Property for map metadata */
    public static final String PROP_MAP_METADATA = "metadata";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.dao.mongodb.MongoConverter#convert(java.lang.Object)
     */
    public BasicDBObject convert(ISite source) {
	return MongoSite.toDBObject(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.dao.mongodb.MongoConverter#convert(com.mongodb.DBObject)
     */
    public ISite convert(DBObject source) {
	return MongoSite.fromDBObject(source);
    }

    /**
     * Copy information from SPI into Mongo DBObject.
     * 
     * @param source
     * @param target
     */
    public static void toDBObject(ISite source, BasicDBObject target) {
	target.append(PROP_NAME, source.getName());
	target.append(PROP_DESCRIPTION, source.getDescription());
	target.append(PROP_IMAGE_URL, source.getImageUrl());
	target.append(PROP_TOKEN, source.getToken());

	BasicDBObject mapData = new BasicDBObject();
	mapData.append(PROP_MAP_TYPE, source.getMap().getType());
	MongoMetadataProvider.toDBObject(PROP_MAP_METADATA, source.getMap(), mapData);
	target.append(PROP_MAP_DATA, mapData);

	MongoSiteWhereEntity.toDBObject(source, target);
	MongoMetadataProvider.toDBObject(source, target);
    }

    /**
     * Copy information from Mongo DBObject to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDBObject(DBObject source, Site target) {
	String name = (String) source.get(PROP_NAME);
	String description = (String) source.get(PROP_DESCRIPTION);
	String imageUrl = (String) source.get(PROP_IMAGE_URL);
	String token = (String) source.get(PROP_TOKEN);

	DBObject mdo = (DBObject) source.get(PROP_MAP_DATA);
	if (mdo != null) {
	    SiteMapData mapData = new SiteMapData();
	    MongoMetadataProvider.fromDBObject(PROP_MAP_METADATA, mdo, mapData);
	    String type = (String) mdo.get(PROP_MAP_TYPE);
	    mapData.setType(type);
	    target.setMap(mapData);
	}

	target.setName(name);
	target.setDescription(description);
	target.setImageUrl(imageUrl);
	target.setToken(token);

	MongoSiteWhereEntity.fromDBObject(source, target);
	MongoMetadataProvider.fromDBObject(source, target);
    }

    /**
     * Convert SPI object to Mongo DBObject.
     * 
     * @param source
     * @return
     */
    public static BasicDBObject toDBObject(ISite source) {
	BasicDBObject result = new BasicDBObject();
	MongoSite.toDBObject(source, result);
	return result;
    }

    /**
     * Convert a DBObject into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static Site fromDBObject(DBObject source) {
	Site result = new Site();
	MongoSite.fromDBObject(source, result);
	return result;
    }
}