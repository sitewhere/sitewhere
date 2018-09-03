/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.mongodb;

import java.util.List;
import java.util.UUID;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.area.AreaType;
import com.sitewhere.spi.area.IAreaType;

/**
 * Used to load or save area type data to MongoDB.
 * 
 * @author dadams
 */
public class MongoAreaType implements MongoConverter<IAreaType> {

    /** Property for name */
    public static final String PROP_NAME = "name";

    /** Property for description */
    public static final String PROP_DESCRIPTION = "desc";

    /** Property for icon */
    public static final String PROP_ICON = "icon";

    /** Property for contained area type ids */
    public static final String PROP_CONTAINED_AREA_TYPE_IDS = "caty";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.dao.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IAreaType source) {
	return MongoAreaType.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IAreaType convert(Document source) {
	return MongoAreaType.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IAreaType source, Document target) {
	target.append(PROP_NAME, source.getName());
	target.append(PROP_DESCRIPTION, source.getDescription());
	target.append(PROP_ICON, source.getIcon());
	target.append(PROP_CONTAINED_AREA_TYPE_IDS, source.getContainedAreaTypeIds());

	MongoSiteWhereEntity.toDocument(source, target);
	MongoMetadataProvider.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    @SuppressWarnings("unchecked")
    public static void fromDocument(Document source, AreaType target) {
	String name = (String) source.get(PROP_NAME);
	String description = (String) source.get(PROP_DESCRIPTION);
	String icon = (String) source.get(PROP_ICON);
	List<UUID> containedAreaTypeIds = (List<UUID>) source.get(PROP_CONTAINED_AREA_TYPE_IDS);

	target.setName(name);
	target.setDescription(description);
	target.setIcon(icon);
	target.setContainedAreaTypeIds(containedAreaTypeIds);

	MongoSiteWhereEntity.fromDocument(source, target);
	MongoMetadataProvider.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IAreaType source) {
	Document result = new Document();
	MongoAreaType.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static AreaType fromDocument(Document source) {
	AreaType result = new AreaType();
	MongoAreaType.fromDocument(source, result);
	return result;
    }
}