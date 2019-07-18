/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.persistence.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.spi.SiteWhereException;

/**
 * Mongo client that provides asset management collections.
 */
public interface IAssetManagementMongoClient {

    /** Default collection name for asset types */
    public static final String DEFAULT_ASSET_TYPES_COLLECTION_NAME = "assettypes";

    /** Default collection name for assets */
    public static final String DEFAULT_ASSETS_COLLECTION_NAME = "assets";

    /**
     * Get asset types collection.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getAssetTypesCollection() throws SiteWhereException;

    /**
     * Get assets collection.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getAssetsCollection() throws SiteWhereException;
}