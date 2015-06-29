/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.asset;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.sitewhere.mongodb.IAssetManagementMongoClient;
import com.sitewhere.mongodb.device.MongoDeviceManagement;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetSearchCriteria;
import com.sitewhere.spi.asset.IHardwareAsset;
import com.sitewhere.spi.asset.ILocationAsset;
import com.sitewhere.spi.asset.IPersonAsset;
import com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest;
import com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest;
import com.sitewhere.spi.asset.request.ILocationAssetCreateRequest;
import com.sitewhere.spi.asset.request.IPersonAssetCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IAssetManagement} that stores data in MongoDB.
 * 
 * @author Derek
 */
public class MongoAssetManagement extends LifecycleComponent implements IAssetManagement {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(MongoDeviceManagement.class);

	/** Injected with global SiteWhere Mongo client */
	private IAssetManagementMongoClient mongoClient;

	public MongoAssetManagement() {
		super(LifecycleComponentType.DataStore);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		ensureIndexes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
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

	/**
	 * Ensure that expected collection indexes exist.
	 * 
	 * @throws SiteWhereException
	 */
	protected void ensureIndexes() throws SiteWhereException {
		getMongoClient().getAssetCategoriesCollection().createIndex(
				new BasicDBObject(MongoAssetCategory.PROP_ID, 1), new BasicDBObject("unique", true));
		getMongoClient().getAssetsCollection().createIndex(
				(new BasicDBObject(MongoAsset.PROP_CATEGORY_ID, 1)).append(MongoAsset.PROP_ID, 1),
				new BasicDBObject("unique", true));
	}

	@Override
	public IAssetCategory createAssetCategory(IAssetCategoryCreateRequest request) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAssetCategory getAssetCategory(String categoryId) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISearchResults<IAssetCategory> listAssetCategories(ISearchCriteria criteria)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAssetCategory deleteAssetCategory(String categoryId) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPersonAsset createPersonAsset(String categoryId, IPersonAssetCreateRequest request)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IHardwareAsset createHardwareAsset(String categoryId, IHardwareAssetCreateRequest request)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILocationAsset createLocationAsset(String categoryId, ILocationAssetCreateRequest request)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAsset getAsset(String categoryId, String assetId) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAsset deleteAsset(String categoryId, String assetId) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISearchResults<IAsset> listAssets(String categoryId, IAssetSearchCriteria criteria)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	public IAssetManagementMongoClient getMongoClient() {
		return mongoClient;
	}

	public void setMongoClient(IAssetManagementMongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}
}