/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.common;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.common.IMetadataProvider;

/**
 * Used to load or save device entity metdata to MongoDB.
 * 
 * @author dadams
 */
public class MongoMetadataProvider {

    /** Property for entity metadata */
    public static final String PROP_METADATA = "metadata";

    /**
     * Store data into a DBObject using default property name.
     * 
     * @param source
     * @param target
     */
    public static void toDBObject(IMetadataProvider source, DBObject target) {
	MongoMetadataProvider.toDBObject(PROP_METADATA, source, target);
    }

    /**
     * Store data into a DBObject.
     * 
     * @param propertyName
     * @param source
     * @param target
     */
    public static void toDBObject(String propertyName, IMetadataProvider source, DBObject target) {
	BasicDBObject metadata = new BasicDBObject();
	for (String key : source.getMetadata().keySet()) {
	    metadata.put(key, source.getMetadata(key));
	}
	target.put(propertyName, metadata);
    }

    /**
     * Load data from a DBObject using default property name.
     * 
     * @param source
     * @param target
     */
    public static void fromDBObject(DBObject source, IMetadataProvider target) {
	MongoMetadataProvider.fromDBObject(PROP_METADATA, source, target);
    }

    /**
     * Load data from a DBObject.
     * 
     * @param PropertyName
     * @param source
     * @param target
     */
    public static void fromDBObject(String propertyName, DBObject source, IMetadataProvider target) {
	DBObject metadata = (DBObject) source.get(propertyName);
	if (metadata != null) {
	    for (String key : metadata.keySet()) {
		try {
		    target.addOrReplaceMetadata(key, (String) metadata.get(key));
		} catch (SiteWhereException e) {
		    // Skip field if key is invalid in the database.
		}
	    }
	}
    }
}