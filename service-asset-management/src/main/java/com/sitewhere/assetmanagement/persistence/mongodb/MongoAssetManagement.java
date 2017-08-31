/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.assetmanagement.persistence.mongodb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.mongodb.IMongoConverterLookup;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.rest.model.asset.AssetCategory;
import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.asset.LocationAsset;
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
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
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IAssetManagement} that stores data in MongoDB.
 * 
 * @author Derek
 */
public class MongoAssetManagement extends TenantLifecycleComponent implements IAssetManagement {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Converter lookup */
    private static IMongoConverterLookup LOOKUP = new MongoConverters();

    /** Injected with global SiteWhere Mongo client */
    private IAssetManagementMongoClient mongoClient;

    public MongoAssetManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	ensureIndexes();
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
	getMongoClient().getAssetCategoriesCollection(getTenant())
		.createIndex(Indexes.ascending(MongoAssetCategory.PROP_ID), new IndexOptions().unique(true));
	getMongoClient().getAssetsCollection(getTenant()).createIndex(Indexes
		.compoundIndex(Indexes.ascending(MongoAsset.PROP_CATEGORY_ID), Indexes.ascending(MongoAsset.PROP_ID)),
		new IndexOptions().unique(true));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#createAssetCategory(com.
     * sitewhere.spi. asset.request.IAssetCategoryCreateRequest)
     */
    @Override
    public IAssetCategory createAssetCategory(IAssetCategoryCreateRequest request) throws SiteWhereException {
	// Use common logic so all backend implementations work the same.
	AssetCategory category = SiteWherePersistence.assetCategoryCreateLogic(request);

	MongoCollection<Document> categories = getMongoClient().getAssetCategoriesCollection(getTenant());
	Document created = MongoAssetCategory.toDocument(category);
	MongoPersistence.insert(categories, created, ErrorCode.AssetCategoryIdInUse);

	return MongoAssetCategory.fromDocument(created);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#getAssetCategory(java.lang.
     * String)
     */
    @Override
    public IAssetCategory getAssetCategory(String id) throws SiteWhereException {
	Document dbCategory = getAssetCategoryDocument(id);
	if (dbCategory != null) {
	    return MongoAssetCategory.fromDocument(dbCategory);
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#updateAssetCategory(java.lang.
     * String, com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest)
     */
    @Override
    public IAssetCategory updateAssetCategory(String categoryId, IAssetCategoryCreateRequest request)
	    throws SiteWhereException {
	Document match = assertAssetCategory(categoryId);
	AssetCategory category = MongoAssetCategory.fromDocument(match);

	// Use common update logic.
	SiteWherePersistence.assetCategoryUpdateLogic(request, category);
	Document updated = MongoAssetCategory.toDocument(category);

	Document query = new Document(MongoAssetCategory.PROP_ID, categoryId);
	MongoCollection<Document> categories = getMongoClient().getAssetCategoriesCollection(getTenant());
	MongoPersistence.update(categories, query, updated);

	return MongoAssetCategory.fromDocument(updated);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#listAssetCategories(com.
     * sitewhere.spi. search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IAssetCategory> listAssetCategories(ISearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> categories = getMongoClient().getAssetCategoriesCollection(getTenant());
	Document query = new Document();
	Document sort = new Document(MongoAssetCategory.PROP_NAME, 1).append(MongoAssetCategory.PROP_ASSET_TYPE, 1);
	return MongoPersistence.search(IAssetCategory.class, categories, query, sort, criteria, LOOKUP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#deleteAssetCategory(java.lang.
     * String)
     */
    @Override
    public IAssetCategory deleteAssetCategory(String categoryId) throws SiteWhereException {
	Document existing = assertAssetCategory(categoryId);
	MongoCollection<Document> categories = getMongoClient().getAssetCategoriesCollection(getTenant());
	MongoPersistence.delete(categories, existing);

	return MongoAssetCategory.fromDocument(existing);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#createPersonAsset(java.lang.
     * String, com.sitewhere.spi.asset.request.IPersonAssetCreateRequest)
     */
    @Override
    public IPersonAsset createPersonAsset(String categoryId, IPersonAssetCreateRequest request)
	    throws SiteWhereException {
	// Use common logic so all backend implementations work the same.
	Document db = assertAssetCategory(categoryId);
	IAssetCategory category = MongoAssetCategory.fromDocument(db);
	PersonAsset person = SiteWherePersistence.personAssetCreateLogic(category, request);

	MongoCollection<Document> assets = getMongoClient().getAssetsCollection(getTenant());
	Document created = MongoPersonAsset.toDocument(person);
	MongoPersistence.insert(assets, created, ErrorCode.AssetIdInUse);

	return MongoPersonAsset.fromDocument(created);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#updatePersonAsset(java.lang.
     * String, java.lang.String,
     * com.sitewhere.spi.asset.request.IPersonAssetCreateRequest)
     */
    @Override
    public IPersonAsset updatePersonAsset(String categoryId, String assetId, IPersonAssetCreateRequest request)
	    throws SiteWhereException {
	Document dbAsset = assertAsset(categoryId, assetId);
	PersonAsset person = (PersonAsset) unmarshalAsset(dbAsset);

	// Use common logic so all backend implementations work the same.
	SiteWherePersistence.personAssetUpdateLogic(person, request);
	Document updated = MongoPersonAsset.toDocument(person);

	Document query = new Document(MongoAsset.PROP_CATEGORY_ID, categoryId).append(MongoAsset.PROP_ID, assetId);
	MongoCollection<Document> assets = getMongoClient().getAssetsCollection(getTenant());
	MongoPersistence.update(assets, query, updated);

	return MongoPersonAsset.fromDocument(updated);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#createHardwareAsset(java.lang.
     * String, com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest)
     */
    @Override
    public IHardwareAsset createHardwareAsset(String categoryId, IHardwareAssetCreateRequest request)
	    throws SiteWhereException {
	// Use common logic so all backend implementations work the same.
	Document db = assertAssetCategory(categoryId);
	IAssetCategory category = MongoAssetCategory.fromDocument(db);
	HardwareAsset hw = SiteWherePersistence.hardwareAssetCreateLogic(category, request);

	MongoCollection<Document> assets = getMongoClient().getAssetsCollection(getTenant());
	Document created = MongoHardwareAsset.toDocument(hw);
	MongoPersistence.insert(assets, created, ErrorCode.AssetIdInUse);

	return MongoHardwareAsset.fromDocument(created);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#updateHardwareAsset(java.lang.
     * String, java.lang.String,
     * com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest)
     */
    @Override
    public IHardwareAsset updateHardwareAsset(String categoryId, String assetId, IHardwareAssetCreateRequest request)
	    throws SiteWhereException {
	Document dbAsset = assertAsset(categoryId, assetId);
	HardwareAsset hardware = (HardwareAsset) unmarshalAsset(dbAsset);

	// Use common logic so all backend implementations work the same.
	SiteWherePersistence.hardwareAssetUpdateLogic(hardware, request);
	Document updated = MongoHardwareAsset.toDocument(hardware);

	Document query = new Document(MongoAsset.PROP_CATEGORY_ID, categoryId).append(MongoAsset.PROP_ID, assetId);
	MongoCollection<Document> assets = getMongoClient().getAssetsCollection(getTenant());
	MongoPersistence.update(assets, query, updated);

	return MongoHardwareAsset.fromDocument(updated);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#createLocationAsset(java.lang.
     * String, com.sitewhere.spi.asset.request.ILocationAssetCreateRequest)
     */
    @Override
    public ILocationAsset createLocationAsset(String categoryId, ILocationAssetCreateRequest request)
	    throws SiteWhereException {
	// Use common logic so all backend implementations work the same.
	Document db = assertAssetCategory(categoryId);
	IAssetCategory category = MongoAssetCategory.fromDocument(db);
	LocationAsset loc = SiteWherePersistence.locationAssetCreateLogic(category, request);

	MongoCollection<Document> assets = getMongoClient().getAssetsCollection(getTenant());
	Document created = MongoLocationAsset.toDocument(loc);
	MongoPersistence.insert(assets, created, ErrorCode.AssetIdInUse);

	return MongoLocationAsset.fromDocument(created);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#updateLocationAsset(java.lang.
     * String, java.lang.String,
     * com.sitewhere.spi.asset.request.ILocationAssetCreateRequest)
     */
    @Override
    public ILocationAsset updateLocationAsset(String categoryId, String assetId, ILocationAssetCreateRequest request)
	    throws SiteWhereException {
	Document dbAsset = assertAsset(categoryId, assetId);
	LocationAsset location = (LocationAsset) unmarshalAsset(dbAsset);

	// Use common logic so all backend implementations work the same.
	SiteWherePersistence.locationAssetUpdateLogic(location, request);
	Document updated = MongoLocationAsset.toDocument(location);

	Document query = new Document(MongoAsset.PROP_CATEGORY_ID, categoryId).append(MongoAsset.PROP_ID, assetId);
	MongoCollection<Document> assets = getMongoClient().getAssetsCollection(getTenant());
	MongoPersistence.update(assets, query, updated);

	return MongoLocationAsset.fromDocument(updated);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagement#getAsset(java.lang.String,
     * java.lang.String)
     */
    @Override
    public IAsset getAsset(String categoryId, String assetId) throws SiteWhereException {
	Document dbAsset = getAssetDocument(categoryId, assetId);
	if (dbAsset == null) {
	    return null;
	}
	return unmarshalAsset(dbAsset);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#deleteAsset(java.lang.String,
     * java.lang.String)
     */
    @Override
    public IAsset deleteAsset(String categoryId, String assetId) throws SiteWhereException {
	Document existing = assertAsset(categoryId, assetId);
	MongoCollection<Document> assets = getMongoClient().getAssetsCollection(getTenant());
	MongoPersistence.delete(assets, existing);
	return unmarshalAsset(existing);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#listAssets(java.lang.String,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IAsset> listAssets(String categoryId, ISearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> assets = getMongoClient().getAssetsCollection(getTenant());
	Document query = new Document(MongoAsset.PROP_CATEGORY_ID, categoryId);
	Document sort = new Document(MongoAsset.PROP_NAME, 1);
	return MongoPersistence.search(IAsset.class, assets, query, sort, criteria, LOOKUP);
    }

    /**
     * Return the {@link Document} for the asset category with the given id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected Document getAssetCategoryDocument(String id) throws SiteWhereException {
	try {
	    MongoCollection<Document> categories = getMongoClient().getAssetCategoriesCollection(getTenant());
	    Document query = new Document(MongoAssetCategory.PROP_ID, id);
	    return categories.find(query).first();
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    /**
     * Return the {@link Document} for the asset category with the given id.
     * Throws an exception if the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document assertAssetCategory(String id) throws SiteWhereException {
	Document match = getAssetCategoryDocument(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAssetCategoryId, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Retirn the {@link Document} for the given asset. Returns null if not
     * found.
     * 
     * @param categoryId
     * @param assetId
     * @return
     * @throws SiteWhereException
     */
    protected Document getAssetDocument(String categoryId, String assetId) throws SiteWhereException {
	try {
	    MongoCollection<Document> assets = getMongoClient().getAssetsCollection(getTenant());
	    Document query = new Document(MongoAsset.PROP_CATEGORY_ID, categoryId).append(MongoAsset.PROP_ID, assetId);
	    return assets.find(query).first();
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    /**
     * Return the {@link Document} for the asset with the given id. Throws an
     * exception if the token is not valid.
     * 
     * @param categoryId
     * @param assetId
     * @return
     * @throws SiteWhereException
     */
    protected Document assertAsset(String categoryId, String assetId) throws SiteWhereException {
	Document match = getAssetDocument(categoryId, assetId);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAssetId, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Unmarshal an asset from a {@link Document}.
     * 
     * @param dbAsset
     * @return
     * @throws SiteWhereException
     */
    public static Asset unmarshalAsset(Document dbAsset) throws SiteWhereException {
	String strAssetType = (String) dbAsset.get(MongoAsset.PROP_ASSET_TYPE);
	try {
	    AssetType type = AssetType.valueOf(strAssetType);
	    switch (type) {
	    case Device:
	    case Hardware: {
		return MongoHardwareAsset.fromDocument(dbAsset);
	    }
	    case Location: {
		return MongoLocationAsset.fromDocument(dbAsset);
	    }
	    case Person: {
		return MongoPersonAsset.fromDocument(dbAsset);
	    }
	    }
	} catch (IllegalArgumentException e) {
	    throw new SiteWhereException("Unknown asset type: " + strAssetType);
	}
	return null;
    }

    /**
     * Marshal an asset to a {@link Document}.
     * 
     * @param asset
     * @return
     * @throws SiteWhereException
     */
    public static Document marshalAsset(IAsset asset) throws SiteWhereException {
	switch (asset.getType()) {
	case Device:
	case Hardware: {
	    return MongoHardwareAsset.toDocument((IHardwareAsset) asset);
	}
	case Location: {
	    return MongoLocationAsset.toDocument((ILocationAsset) asset);
	}
	case Person: {
	    return MongoPersonAsset.toDocument((IPersonAsset) asset);
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