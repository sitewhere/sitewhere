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
import com.sitewhere.mongodb.common.MongoBrandedEntity;
import com.sitewhere.rest.model.area.Area;
import com.sitewhere.spi.area.IArea;

/**
 * Used to load or save area data to MongoDB.
 */
public class MongoArea implements MongoConverter<IArea> {

    /** Property for area type id */
    public static final String PROP_AREA_TYPE_ID = "atid";

    /** Property for parent area id */
    public static final String PROP_PARENT_AREA_ID = "paid";

    /** Property for name */
    public static final String PROP_NAME = "name";

    /** Property for description */
    public static final String PROP_DESCRIPTION = "desc";

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
	target.append(PROP_AREA_TYPE_ID, source.getAreaTypeId());
	target.append(PROP_PARENT_AREA_ID, source.getParentId());
	target.append(PROP_NAME, source.getName());
	target.append(PROP_DESCRIPTION, source.getDescription());

	MongoBoundedEntity.saveBounds(source, target);
	MongoBrandedEntity.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, Area target) {
	UUID areaTypeId = (UUID) source.get(PROP_AREA_TYPE_ID);
	UUID parentAreaId = (UUID) source.get(PROP_PARENT_AREA_ID);
	String name = (String) source.get(PROP_NAME);
	String description = (String) source.get(PROP_DESCRIPTION);

	target.setAreaTypeId(areaTypeId);
	target.setParentId(parentAreaId);
	target.setName(name);
	target.setDescription(description);
	target.setBounds(MongoBoundedEntity.loadBounds(source));

	MongoBrandedEntity.fromDocument(source, target);
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