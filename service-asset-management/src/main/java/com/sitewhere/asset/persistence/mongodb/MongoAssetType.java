/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.persistence.mongodb;

import java.util.UUID;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.asset.AssetType;
import com.sitewhere.spi.asset.AssetCategory;
import com.sitewhere.spi.asset.IAssetType;

/**
 * Used to load or save asset type information to MongoDB.
 * 
 * @author Derek
 */
public class MongoAssetType implements MongoConverter<IAssetType> {

    /** Property for asset type id */
    public static final String PROP_ID = "_id";

    /** Property for token */
    public static final String PROP_TOKEN = "tokn";

    /** Property for name */
    public static final String PROP_NAME = "name";

    /** Property for description */
    public static final String PROP_DESCRIPTION = "desc";

    /** Property for asset type image URL */
    public static final String PROP_IMAGE_URL = "imgu";

    /** Property for asset category */
    public static final String PROP_ASSET_CATEGORY = "asct";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IAssetType source) {
	return MongoAssetType.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IAssetType convert(Document source) {
	return MongoAssetType.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IAssetType source, Document target) {
	target.append(PROP_ID, source.getId());
	target.append(PROP_TOKEN, source.getToken());
	target.append(PROP_NAME, source.getName());
	target.append(PROP_DESCRIPTION, source.getDescription());
	target.append(PROP_IMAGE_URL, source.getImageUrl());
	target.append(PROP_ASSET_CATEGORY, source.getAssetCategory().name());

	MongoSiteWhereEntity.toDocument(source, target);
	MongoMetadataProvider.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, AssetType target) {
	UUID id = (UUID) source.get(PROP_ID);
	String token = (String) source.get(PROP_TOKEN);
	String name = (String) source.get(PROP_NAME);
	String description = (String) source.get(PROP_DESCRIPTION);
	String imageUrl = (String) source.get(PROP_IMAGE_URL);

	String assetCategoryStr = (String) source.get(PROP_ASSET_CATEGORY);
	AssetCategory category = (assetCategoryStr != null) ? AssetCategory.valueOf(assetCategoryStr) : null;

	target.setId(id);
	target.setToken(token);
	target.setName(name);
	target.setDescription(description);
	target.setImageUrl(imageUrl);
	target.setAssetCategory(category);

	MongoSiteWhereEntity.fromDocument(source, target);
	MongoMetadataProvider.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IAssetType source) {
	Document result = new Document();
	MongoAssetType.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static AssetType fromDocument(Document source) {
	if (source == null) {
	    return null;
	}
	AssetType result = new AssetType();
	MongoAssetType.fromDocument(source, result);
	return result;
    }
}