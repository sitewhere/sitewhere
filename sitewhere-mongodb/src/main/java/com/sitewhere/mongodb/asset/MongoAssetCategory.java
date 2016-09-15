/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.asset;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
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
    public BasicDBObject convert(IAssetCategory source) {
	return MongoAssetCategory.toDBObject(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(com.mongodb.DBObject)
     */
    @Override
    public IAssetCategory convert(DBObject source) {
	return MongoAssetCategory.fromDBObject(source);
    }

    /**
     * Copy information from SPI into Mongo DBObject.
     * 
     * @param source
     * @param target
     */
    public static void toDBObject(IAssetCategory source, BasicDBObject target) {
	target.append(PROP_ID, source.getId());
	target.append(PROP_NAME, source.getName());
	target.append(PROP_ASSET_TYPE, source.getAssetType().name());
    }

    /**
     * Copy information from Mongo DBObject to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDBObject(DBObject source, AssetCategory target) {
	String id = (String) source.get(PROP_ID);
	String name = (String) source.get(PROP_NAME);
	String assetTypeStr = (String) source.get(PROP_ASSET_TYPE);

	target.setId(id);
	target.setName(name);
	target.setAssetType(AssetType.valueOf(assetTypeStr));
    }

    /**
     * Convert SPI object to Mongo DBObject.
     * 
     * @param source
     * @return
     */
    public static BasicDBObject toDBObject(IAssetCategory source) {
	BasicDBObject result = new BasicDBObject();
	MongoAssetCategory.toDBObject(source, result);
	return result;
    }

    /**
     * Convert a DBObject into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static AssetCategory fromDBObject(DBObject source) {
	AssetCategory result = new AssetCategory();
	MongoAssetCategory.fromDBObject(source, result);
	return result;
    }
}