/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.persistence.mongodb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.configuration.instance.mongodb.MongoConfiguration;
import com.sitewhere.mongodb.BaseMongoClient;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Mongo client that provides access to asset management resources.
 * 
 * @author Derek
 */
public class AssetManagementMongoClient extends BaseMongoClient implements IAssetManagementMongoClient {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Injected name used for asset categories collection */
    private String assetCategoriesCollectionName = IAssetManagementMongoClient.DEFAULT_ASSET_CATEGORIES_COLLECTION_NAME;

    /** Injected name used for assets collection */
    private String assetsCollectionName = IAssetManagementMongoClient.DEFAULT_ASSETS_COLLECTION_NAME;

    public AssetManagementMongoClient(MongoConfiguration configuration) {
	super(configuration);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IAssetManagementMongoClient#
     * getAssetCategoriesCollection( com.sitewhere.spi.user.ITenant)
     */
    public MongoCollection<Document> getAssetCategoriesCollection(ITenant tenant) throws SiteWhereException {
	return getTenantDatabase(tenant).getCollection(getAssetCategoriesCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.mongodb.IAssetManagementMongoClient#getAssetsCollection(com
     * .sitewhere .spi.user.ITenant)
     */
    public MongoCollection<Document> getAssetsCollection(ITenant tenant) throws SiteWhereException {
	return getTenantDatabase(tenant).getCollection(getAssetsCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public String getAssetCategoriesCollectionName() {
	return assetCategoriesCollectionName;
    }

    public void setAssetCategoriesCollectionName(String assetCategoriesCollectionName) {
	this.assetCategoriesCollectionName = assetCategoriesCollectionName;
    }

    public String getAssetsCollectionName() {
	return assetsCollectionName;
    }

    public void setAssetsCollectionName(String assetsCollectionName) {
	this.assetsCollectionName = assetsCollectionName;
    }
}