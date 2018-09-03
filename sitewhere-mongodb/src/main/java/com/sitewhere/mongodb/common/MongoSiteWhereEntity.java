/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.common;

import java.util.Date;
import java.util.UUID;

import org.bson.Document;

import com.sitewhere.rest.model.common.PersistentEntity;
import com.sitewhere.spi.common.IPersistentEntity;

/**
 * Used to load or save SiteWhereEntity data to MongoDB.
 * 
 * @author dadams
 */
public class MongoSiteWhereEntity {

    /** Property for tenant id */
    public static final String PROP_ID = "_id";

    /** Property for token */
    public static final String PROP_TOKEN = "tokn";

    /** Property for date entity was created */
    public static final String PROP_CREATED_DATE = "crdt";

    /** Property for user that created entity */
    public static final String PROP_CREATED_BY = "crby";

    /** Property for date entity was last updated */
    public static final String PROP_UPDATED_DATE = "updt";

    /** Property for user that updated entity */
    public static final String PROP_UPDATED_BY = "upby";

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IPersistentEntity source, Document target) {
	target.append(PROP_ID, source.getId());
	target.append(PROP_TOKEN, source.getToken());
	if (source.getCreatedDate() != null) {
	    target.append(PROP_CREATED_DATE, source.getCreatedDate());
	}
	if (source.getCreatedBy() != null) {
	    target.append(PROP_CREATED_BY, source.getCreatedBy());
	}
	if (source.getUpdatedDate() != null) {
	    target.append(PROP_UPDATED_DATE, source.getUpdatedDate());
	}
	if (source.getUpdatedBy() != null) {
	    target.append(PROP_UPDATED_BY, source.getUpdatedBy());
	}
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, PersistentEntity target) {
	UUID id = (UUID) source.get(PROP_ID);
	String token = (String) source.get(PROP_TOKEN);
	Date createdDate = (Date) source.get(PROP_CREATED_DATE);
	String createdBy = (String) source.get(PROP_CREATED_BY);
	Date updatedDate = (Date) source.get(PROP_UPDATED_DATE);
	String updatedBy = (String) source.get(PROP_UPDATED_BY);

	target.setId(id);
	target.setToken(token);
	target.setCreatedDate(createdDate);
	target.setCreatedBy(createdBy);
	target.setUpdatedDate(updatedDate);
	target.setUpdatedBy(updatedBy);
    }
}