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
import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.spi.asset.IAsset;

/**
 * Used to load or save asset information to MongoDB.
 * 
 * @author Derek
 */
public class MongoAsset implements MongoConverter<IAsset> {

    /** Property for asset id */
    public static final String PROP_ID = "_id";

    /** Property for token */
    public static final String PROP_TOKEN = "tokn";

    /** Property for asset type id */
    public static final String PROP_ASSET_TYPE_ID = "atid";

    /** Property for asset name */
    public static final String PROP_NAME = "name";

    /** Property for asset image URL */
    public static final String PROP_IMAGE_URL = "image";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(IAsset source) {
	return MongoAsset.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public IAsset convert(Document source) {
	return MongoAsset.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(IAsset source, Document target) {
	target.append(PROP_ID, source.getId());
	target.append(PROP_TOKEN, source.getToken());
	target.append(PROP_ASSET_TYPE_ID, source.getAssetTypeId());
	target.append(PROP_NAME, source.getName());
	target.append(PROP_IMAGE_URL, source.getImageUrl());

	MongoSiteWhereEntity.toDocument(source, target);
	MongoMetadataProvider.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    public static void fromDocument(Document source, Asset target) {
	UUID id = (UUID) source.get(PROP_ID);
	String token = (String) source.get(PROP_TOKEN);
	UUID assetTypeId = (UUID) source.get(PROP_ASSET_TYPE_ID);
	String name = (String) source.get(PROP_NAME);
	String imageUrl = (String) source.get(PROP_IMAGE_URL);

	target.setId(id);
	target.setToken(token);
	target.setAssetTypeId(assetTypeId);
	target.setName(name);
	target.setImageUrl(imageUrl);

	MongoSiteWhereEntity.fromDocument(source, target);
	MongoMetadataProvider.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(IAsset source) {
	Document result = new Document();
	MongoAsset.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static Asset fromDocument(Document source) {
	Asset result = new Asset();
	MongoAsset.fromDocument(source, result);
	return result;
    }
}