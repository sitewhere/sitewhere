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
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoTimeoutException;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.mongodb.IAssetManagementMongoClient;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.mongodb.device.MongoDeviceManagement;
import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.rest.model.asset.AssetCategory;
import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.asset.LocationAsset;
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IHardwareAsset;
import com.sitewhere.spi.asset.ILocationAsset;
import com.sitewhere.spi.asset.IPersonAsset;
import com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest;
import com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest;
import com.sitewhere.spi.asset.request.ILocationAssetCreateRequest;
import com.sitewhere.spi.asset.request.IPersonAssetCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.asset.IAssetManagement#createAssetCategory(com.sitewhere.spi.
	 * asset.request.IAssetCategoryCreateRequest)
	 */
	@Override
	public IAssetCategory createAssetCategory(IAssetCategoryCreateRequest request) throws SiteWhereException {
		IAssetCategory existing = getAssetCategory(request.getId());
		if (existing != null) {
			throw new SiteWhereSystemException(ErrorCode.AssetCategoryIdInUse, ErrorLevel.ERROR);
		}

		// Use common logic so all backend implementations work the same.
		AssetCategory category = SiteWherePersistence.assetCategoryCreateLogic(request);

		DBCollection categories = getMongoClient().getAssetCategoriesCollection();
		DBObject created = MongoAssetCategory.toDBObject(category);
		MongoPersistence.insert(categories, created);

		return MongoAssetCategory.fromDBObject(created);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetManagement#getAssetCategory(java.lang.String)
	 */
	@Override
	public IAssetCategory getAssetCategory(String id) throws SiteWhereException {
		DBObject dbCategory = getAssetCategoryDBObject(id);
		if (dbCategory != null) {
			return MongoAssetCategory.fromDBObject(dbCategory);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetManagement#updateAssetCategory(java.lang.String,
	 * com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest)
	 */
	@Override
	public IAssetCategory updateAssetCategory(String categoryId, IAssetCategoryCreateRequest request)
			throws SiteWhereException {
		DBObject match = assertAssetCategory(categoryId);
		AssetCategory category = MongoAssetCategory.fromDBObject(match);

		// Use common update logic so that backend implemetations act the same way.
		SiteWherePersistence.assetCategoryUpdateLogic(request, category);
		DBObject updated = MongoAssetCategory.toDBObject(category);

		BasicDBObject query = new BasicDBObject(MongoAssetCategory.PROP_ID, categoryId);
		DBCollection categories = getMongoClient().getAssetCategoriesCollection();
		MongoPersistence.update(categories, query, updated);

		return MongoAssetCategory.fromDBObject(updated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.asset.IAssetManagement#listAssetCategories(com.sitewhere.spi.
	 * search.ISearchCriteria)
	 */
	@Override
	public ISearchResults<IAssetCategory> listAssetCategories(ISearchCriteria criteria)
			throws SiteWhereException {
		DBCollection categories = getMongoClient().getAssetCategoriesCollection();
		BasicDBObject query = new BasicDBObject();
		BasicDBObject sort =
				new BasicDBObject(MongoAssetCategory.PROP_NAME, 1).append(MongoAssetCategory.PROP_ASSET_TYPE,
						1);
		return MongoPersistence.search(IAssetCategory.class, categories, query, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetManagement#deleteAssetCategory(java.lang.String)
	 */
	@Override
	public IAssetCategory deleteAssetCategory(String categoryId) throws SiteWhereException {
		DBObject existing = assertAssetCategory(categoryId);
		DBCollection categories = getMongoClient().getAssetCategoriesCollection();
		MongoPersistence.delete(categories, existing);
		return MongoAssetCategory.fromDBObject(existing);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetManagement#createPersonAsset(java.lang.String,
	 * com.sitewhere.spi.asset.request.IPersonAssetCreateRequest)
	 */
	@Override
	public IPersonAsset createPersonAsset(String categoryId, IPersonAssetCreateRequest request)
			throws SiteWhereException {
		// Use common logic so all backend implementations work the same.
		checkForExistingAsset(categoryId, request.getId());
		IAssetCategory category = getAssetCategory(categoryId);
		PersonAsset person = SiteWherePersistence.personAssetCreateLogic(category, request);

		DBCollection assets = getMongoClient().getAssetsCollection();
		DBObject created = MongoPersonAsset.toDBObject(person);
		MongoPersistence.insert(assets, created);

		return MongoPersonAsset.fromDBObject(created);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetManagement#updatePersonAsset(java.lang.String,
	 * java.lang.String, com.sitewhere.spi.asset.request.IPersonAssetCreateRequest)
	 */
	@Override
	public IPersonAsset updatePersonAsset(String categoryId, String assetId, IPersonAssetCreateRequest request)
			throws SiteWhereException {
		DBObject dbAsset = assertAsset(categoryId, assetId);
		PersonAsset person = (PersonAsset) unmarshalAsset(dbAsset);

		// Use common logic so all backend implementations work the same.
		SiteWherePersistence.personAssetUpdateLogic(person, request);
		DBObject updated = MongoPersonAsset.toDBObject(person);

		BasicDBObject query =
				new BasicDBObject(MongoAsset.PROP_CATEGORY_ID, categoryId).append(MongoAsset.PROP_ID, assetId);
		DBCollection assets = getMongoClient().getAssetsCollection();
		MongoPersistence.update(assets, query, updated);

		return MongoPersonAsset.fromDBObject(updated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetManagement#createHardwareAsset(java.lang.String,
	 * com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest)
	 */
	@Override
	public IHardwareAsset createHardwareAsset(String categoryId, IHardwareAssetCreateRequest request)
			throws SiteWhereException {
		// Use common logic so all backend implementations work the same.
		checkForExistingAsset(categoryId, request.getId());
		IAssetCategory category = getAssetCategory(categoryId);
		HardwareAsset hw = SiteWherePersistence.hardwareAssetCreateLogic(category, request);

		DBCollection assets = getMongoClient().getAssetsCollection();
		DBObject created = MongoHardwareAsset.toDBObject(hw);
		MongoPersistence.insert(assets, created);

		return MongoHardwareAsset.fromDBObject(created);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetManagement#updateHardwareAsset(java.lang.String,
	 * java.lang.String, com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest)
	 */
	@Override
	public IHardwareAsset updateHardwareAsset(String categoryId, String assetId,
			IHardwareAssetCreateRequest request) throws SiteWhereException {
		DBObject dbAsset = assertAsset(categoryId, assetId);
		HardwareAsset hardware = (HardwareAsset) unmarshalAsset(dbAsset);

		// Use common logic so all backend implementations work the same.
		SiteWherePersistence.hardwareAssetUpdateLogic(hardware, request);
		DBObject updated = MongoHardwareAsset.toDBObject(hardware);

		BasicDBObject query =
				new BasicDBObject(MongoAsset.PROP_CATEGORY_ID, categoryId).append(MongoAsset.PROP_ID, assetId);
		DBCollection assets = getMongoClient().getAssetsCollection();
		MongoPersistence.update(assets, query, updated);

		return MongoHardwareAsset.fromDBObject(updated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetManagement#createLocationAsset(java.lang.String,
	 * com.sitewhere.spi.asset.request.ILocationAssetCreateRequest)
	 */
	@Override
	public ILocationAsset createLocationAsset(String categoryId, ILocationAssetCreateRequest request)
			throws SiteWhereException {
		// Use common logic so all backend implementations work the same.
		checkForExistingAsset(categoryId, request.getId());
		IAssetCategory category = getAssetCategory(categoryId);
		LocationAsset loc = SiteWherePersistence.locationAssetCreateLogic(category, request);

		DBCollection assets = getMongoClient().getAssetsCollection();
		DBObject created = MongoLocationAsset.toDBObject(loc);
		MongoPersistence.insert(assets, created);

		return MongoLocationAsset.fromDBObject(created);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetManagement#updateLocationAsset(java.lang.String,
	 * java.lang.String, com.sitewhere.spi.asset.request.ILocationAssetCreateRequest)
	 */
	@Override
	public ILocationAsset updateLocationAsset(String categoryId, String assetId,
			ILocationAssetCreateRequest request) throws SiteWhereException {
		DBObject dbAsset = assertAsset(categoryId, assetId);
		LocationAsset location = (LocationAsset) unmarshalAsset(dbAsset);

		// Use common logic so all backend implementations work the same.
		SiteWherePersistence.locationAssetUpdateLogic(location, request);
		DBObject updated = MongoLocationAsset.toDBObject(location);

		BasicDBObject query =
				new BasicDBObject(MongoAsset.PROP_CATEGORY_ID, categoryId).append(MongoAsset.PROP_ID, assetId);
		DBCollection assets = getMongoClient().getAssetsCollection();
		MongoPersistence.update(assets, query, updated);

		return MongoLocationAsset.fromDBObject(updated);
	}

	/**
	 * If an asset already exists for the given category and id, throw an exception.
	 * 
	 * @param categoryId
	 * @param assetId
	 * @throws SiteWhereException
	 */
	protected void checkForExistingAsset(String categoryId, String assetId) throws SiteWhereException {
		IAsset asset = getAsset(categoryId, assetId);
		if (asset != null) {
			throw new SiteWhereSystemException(ErrorCode.AssetIdInUse, ErrorLevel.ERROR);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetManagement#getAsset(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public IAsset getAsset(String categoryId, String assetId) throws SiteWhereException {
		DBObject dbAsset = getAssetDBObject(categoryId, assetId);
		if (dbAsset == null) {
			return null;
		}
		return unmarshalAsset(dbAsset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetManagement#deleteAsset(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public IAsset deleteAsset(String categoryId, String assetId) throws SiteWhereException {
		DBObject existing = assertAsset(categoryId, assetId);
		DBCollection assets = getMongoClient().getAssetsCollection();
		MongoPersistence.delete(assets, existing);
		return unmarshalAsset(existing);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetManagement#listAssets(java.lang.String,
	 * com.sitewhere.spi.search.ISearchCriteria)
	 */
	@Override
	public ISearchResults<IAsset> listAssets(String categoryId, ISearchCriteria criteria)
			throws SiteWhereException {
		DBCollection assets = getMongoClient().getAssetsCollection();
		BasicDBObject query = new BasicDBObject(MongoAsset.PROP_CATEGORY_ID, categoryId);
		BasicDBObject sort = new BasicDBObject(MongoAsset.PROP_NAME, 1);
		return MongoPersistence.search(IAsset.class, assets, query, sort, criteria);
	}

	/**
	 * Return the {@link DBObject} for the asset category with the given id.
	 * 
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getAssetCategoryDBObject(String id) throws SiteWhereException {
		try {
			DBCollection categories = getMongoClient().getAssetCategoriesCollection();
			BasicDBObject query = new BasicDBObject(MongoAssetCategory.PROP_ID, id);
			DBObject result = categories.findOne(query);
			return result;
		} catch (MongoTimeoutException e) {
			throw new SiteWhereException("Connection to MongoDB lost.", e);
		}
	}

	/**
	 * Return the {@link DBObject} for the asset category with the given id. Throws an
	 * exception if the token is not valid.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject assertAssetCategory(String id) throws SiteWhereException {
		DBObject match = getAssetCategoryDBObject(id);
		if (match == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidAssetCategoryId, ErrorLevel.ERROR);
		}
		return match;
	}

	/**
	 * Retirn the {@link DBObject} for the given asset. Returns null if not found.
	 * 
	 * @param categoryId
	 * @param assetId
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getAssetDBObject(String categoryId, String assetId) throws SiteWhereException {
		try {
			DBCollection assets = getMongoClient().getAssetsCollection();
			BasicDBObject query =
					new BasicDBObject(MongoAsset.PROP_CATEGORY_ID, categoryId).append(MongoAsset.PROP_ID,
							assetId);
			return assets.findOne(query);
		} catch (MongoTimeoutException e) {
			throw new SiteWhereException("Connection to MongoDB lost.", e);
		}
	}

	/**
	 * Return the {@link DBObject} for the asset with the given id. Throws an exception if
	 * the token is not valid.
	 * 
	 * @param categoryId
	 * @param assetId
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject assertAsset(String categoryId, String assetId) throws SiteWhereException {
		DBObject match = getAssetDBObject(categoryId, assetId);
		if (match == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidAssetId, ErrorLevel.ERROR);
		}
		return match;
	}

	/**
	 * Unmarshal an asset from a {@link DBObject}.
	 * 
	 * @param dbAsset
	 * @return
	 * @throws SiteWhereException
	 */
	public static Asset unmarshalAsset(DBObject dbAsset) throws SiteWhereException {
		String strAssetType = (String) dbAsset.get(MongoAsset.PROP_ASSET_TYPE);
		try {
			AssetType type = AssetType.valueOf(strAssetType);
			switch (type) {
			case Device:
			case Hardware: {
				return MongoHardwareAsset.fromDBObject(dbAsset);
			}
			case Location: {
				return MongoLocationAsset.fromDBObject(dbAsset);
			}
			case Person: {
				return MongoPersonAsset.fromDBObject(dbAsset);
			}
			}
		} catch (IllegalArgumentException e) {
			throw new SiteWhereException("Unknown asset type: " + strAssetType);
		}
		return null;
	}

	/**
	 * Marshal an asset to a {@link DBObject}.
	 * 
	 * @param asset
	 * @return
	 * @throws SiteWhereException
	 */
	public static DBObject marshalAsset(IAsset asset) throws SiteWhereException {
		switch (asset.getType()) {
		case Device:
		case Hardware: {
			return MongoHardwareAsset.toDBObject((IHardwareAsset) asset);
		}
		case Location: {
			return MongoLocationAsset.toDBObject((ILocationAsset) asset);
		}
		case Person: {
			return MongoPersonAsset.toDBObject((IPersonAsset) asset);
		}
		default: {
			throw new SiteWhereException("Unhandled asset type: " + asset.getType());
		}
		}
	}

	public IAssetManagementMongoClient getMongoClient() {
		return mongoClient;
	}

	public void setMongoClient(IAssetManagementMongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}
}