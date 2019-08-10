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
import com.sitewhere.mongodb.common.MongoPersistentEntity;
import com.sitewhere.rest.model.area.Zone;
import com.sitewhere.spi.area.IZone;

/**
 * Used to load or save zone data to MongoDB.
 * 
 * @author dadams
 */
public class MongoZone implements MongoConverter<IZone> {

    /** Property for area id */
    public static final String PROP_AREA_ID = "arid";

    /** Property for name */
    public static final String PROP_NAME = "name";

    /** Property for border color */
    public static final String PROP_BORDER_COLOR = "cdcl";

    /** Property for border opacity */
    public static final String PROP_BORDER_OPACITY = "bpac";

    /** Property for fill color */
    public static final String PROP_FILL_COLOR = "flcl";

    /** Property for fill opacity */
    public static final String PROP_FILL_OPACITY = "opac";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.dao.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IZone source) {
	return MongoZone.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IZone convert(Document source) {
	return MongoZone.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IZone source, Document target) {
	target.append(PROP_AREA_ID, source.getAreaId());
	target.append(PROP_NAME, source.getName());
	target.append(PROP_BORDER_COLOR, source.getBorderColor());
	target.append(PROP_BORDER_OPACITY, source.getBorderOpacity());
	target.append(PROP_FILL_COLOR, source.getFillColor());
	target.append(PROP_FILL_OPACITY, source.getFillOpacity());

	MongoBoundedEntity.saveBounds(source, target);
	MongoPersistentEntity.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, Zone target) {
	UUID areaId = (UUID) source.get(PROP_AREA_ID);
	String name = (String) source.get(PROP_NAME);
	String borderColor = (String) source.get(PROP_BORDER_COLOR);
	Double borderOpacity = (Double) source.get(PROP_BORDER_OPACITY);
	String fillColor = (String) source.get(PROP_FILL_COLOR);
	Double fillOpacity = (Double) source.get(PROP_FILL_OPACITY);

	target.setAreaId(areaId);
	target.setName(name);
	target.setBorderColor(borderColor);
	target.setBorderOpacity(borderOpacity);
	target.setFillColor(fillColor);
	target.setFillOpacity(fillOpacity);
	target.setBounds(MongoBoundedEntity.loadBounds(source));

	MongoPersistentEntity.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IZone source) {
	Document result = new Document();
	MongoZone.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static Zone fromDocument(Document source) {
	Zone result = new Zone();
	MongoZone.fromDocument(source, result);
	return result;
    }
}