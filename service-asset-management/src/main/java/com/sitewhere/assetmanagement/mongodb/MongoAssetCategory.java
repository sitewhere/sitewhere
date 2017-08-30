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
import com.sitewhere.rest.model.asset.AssetCategory;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetCategory;

/**
 * Used to load or save asset category information to MongoDB.
 * 
 * @author Derek
 */
public class MongoAssetCategory implements MongoConverter<IAssetCategory> {

    /** Property for category id */
    public static final String PROP_ID = "id";

    /** Property for category name */
    public static final String PROP_NAME = "name";

    /** Property for category asset type */
    public static final String PROP_ASSET_TYPE = "type";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IAssetCategory source) {
	return MongoAssetCategory.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IAssetCategory convert(Document source) {
	return MongoAssetCategory.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IAssetCategory source, Document target) {
	target.append(PROP_ID, source.getId());
	target.append(PROP_NAME, source.getName());
	target.append(PROP_ASSET_TYPE, source.getAssetType().name());
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, AssetCategory target) {
	String id = (String) source.get(PROP_ID);
	String name = (String) source.get(PROP_NAME);
	String assetTypeStr = (String) source.get(PROP_ASSET_TYPE);

	target.setId(id);
	target.setName(name);
	target.setAssetType(AssetType.valueOf(assetTypeStr));
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IAssetCategory source) {
	Document result = new Document();
	MongoAssetCategory.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static AssetCategory fromDocument(Document source) {
	AssetCategory result = new AssetCategory();
	MongoAssetCategory.fromDocument(source, result);
	return result;
    }
}