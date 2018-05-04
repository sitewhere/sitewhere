/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.mongodb;

import java.util.UUID;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.area.Area;
import com.sitewhere.spi.area.IArea;

/**
 * Used to load or save area data to MongoDB.
 * 
 * @author dadams
 */
public class MongoArea implements MongoConverter<IArea> {

    /** Property for id */
    public static final String PROP_ID = "_id";

    /** Property for area type id */
    public static final String PROP_AREA_TYPE_ID = "atid";

    /** Property for parent area id */
    public static final String PROP_PARENT_AREA_ID = "paid";

    /** Property for name */
    public static final String PROP_NAME = "name";

    /** Property for description */
    public static final String PROP_DESCRIPTION = "desc";

    /** Property for image URL */
    public static final String PROP_IMAGE_URL = "imgu";

    /** Property for token */
    public static final String PROP_TOKEN = "tokn";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.dao.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IArea source) {
	return MongoArea.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IArea convert(Document source) {
	return MongoArea.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IArea source, Document target) {
	target.append(PROP_ID, source.getId());
	target.append(PROP_AREA_TYPE_ID, source.getAreaTypeId());
	target.append(PROP_PARENT_AREA_ID, source.getParentAreaId());
	target.append(PROP_NAME, source.getName());
	target.append(PROP_DESCRIPTION, source.getDescription());
	target.append(PROP_IMAGE_URL, source.getImageUrl());
	target.append(PROP_TOKEN, source.getToken());

	MongoBoundedEntity.saveCoordinates(source, target);
	MongoSiteWhereEntity.toDocument(source, target);
	MongoMetadataProvider.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, Area target) {
	UUID id = (UUID) source.get(PROP_ID);
	UUID areaTypeId = (UUID) source.get(PROP_AREA_TYPE_ID);
	UUID parentAreaId = (UUID) source.get(PROP_PARENT_AREA_ID);
	String name = (String) source.get(PROP_NAME);
	String description = (String) source.get(PROP_DESCRIPTION);
	String imageUrl = (String) source.get(PROP_IMAGE_URL);
	String token = (String) source.get(PROP_TOKEN);

	target.setId(id);
	target.setAreaTypeId(areaTypeId);
	target.setParentAreaId(parentAreaId);
	target.setName(name);
	target.setDescription(description);
	target.setImageUrl(imageUrl);
	target.setToken(token);
	target.setCoordinates(MongoBoundedEntity.getCoordinates(source));

	MongoSiteWhereEntity.fromDocument(source, target);
	MongoMetadataProvider.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IArea source) {
	Document result = new Document();
	MongoArea.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static Area fromDocument(Document source) {
	Area result = new Area();
	MongoArea.fromDocument(source, result);
	return result;
    }
}