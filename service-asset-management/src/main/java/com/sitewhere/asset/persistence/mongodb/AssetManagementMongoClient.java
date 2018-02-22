/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.persistence.mongodb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.configuration.instance.mongodb.MongoConfiguration;
import com.sitewhere.mongodb.MongoDbClient;
import com.sitewhere.spi.SiteWhereException;

/**
 * Mongo client that provides access to asset management resources.
 * 
 * @author Derek
 */
public class AssetManagementMongoClient extends MongoDbClient implements IAssetManagementMongoClient {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(AssetManagementMongoClient.class);

    /** Injected name used for asset types collection */
    private String assetTypesCollectionName = IAssetManagementMongoClient.DEFAULT_ASSET_TYPES_COLLECTION_NAME;

    /** Injected name used for assets collection */
    private String assetsCollectionName = IAssetManagementMongoClient.DEFAULT_ASSETS_COLLECTION_NAME;

    public AssetManagementMongoClient(MongoConfiguration configuration) {
	super(configuration);
    }

    /*
     * @see com.sitewhere.asset.persistence.mongodb.IAssetManagementMongoClient#
     * getAssetTypesCollection()
     */
    @Override
    public MongoCollection<Document> getAssetTypesCollection() throws SiteWhereException {
	return getDatabase().getCollection(getAssetTypesCollectionName());
    }

    /*
     * @see com.sitewhere.asset.persistence.mongodb.IAssetManagementMongoClient#
     * getAssetsCollection()
     */
    public MongoCollection<Document> getAssetsCollection() throws SiteWhereException {
	return getDatabase().getCollection(getAssetsCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }

    public String getAssetTypesCollectionName() {
	return assetTypesCollectionName;
    }

    public void setAssetTypesCollectionName(String assetTypesCollectionName) {
	this.assetTypesCollectionName = assetTypesCollectionName;
    }

    public String getAssetsCollectionName() {
	return assetsCollectionName;
    }

    public void setAssetsCollectionName(String assetsCollectionName) {
	this.assetsCollectionName = assetsCollectionName;
    }
}