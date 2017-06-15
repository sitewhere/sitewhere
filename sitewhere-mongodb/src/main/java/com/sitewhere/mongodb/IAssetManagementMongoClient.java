/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Mongo client that provides asset management collections.
 * 
 * @author Derek
 */
public interface IAssetManagementMongoClient {

    /** Default collection name for SiteWhere asset categories */
    public static final String DEFAULT_ASSET_CATEGORIES_COLLECTION_NAME = "assetcategories";

    /** Default collection name for SiteWhere assets */
    public static final String DEFAULT_ASSETS_COLLECTION_NAME = "assets";

    /**
     * Get asset categories collection.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getAssetCategoriesCollection(ITenant tenant) throws SiteWhereException;

    /**
     * Get assets collection.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getAssetsCollection(ITenant tenant) throws SiteWhereException;
}