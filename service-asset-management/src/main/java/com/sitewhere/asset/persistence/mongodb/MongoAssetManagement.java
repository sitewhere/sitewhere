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

import com.mongodb.MongoClientException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.sitewhere.asset.persistence.AssetManagementPersistence;
import com.sitewhere.asset.spi.microservice.IAssetManagementMicroservice;
import com.sitewhere.mongodb.IMongoConverterLookup;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.mongodb.MongoTenantComponent;
import com.sitewhere.mongodb.common.MongoPersistentEntity;
import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.rest.model.asset.AssetType;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.asset.request.IAssetCreateRequest;
import com.sitewhere.spi.asset.request.IAssetTypeCreateRequest;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.asset.IAssetSearchCriteria;
import com.sitewhere.spi.search.asset.IAssetTypeSearchCritiera;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IAssetManagement} that stores data in MongoDB.
 */
public class MongoAssetManagement extends MongoTenantComponent<AssetManagementMongoClient> implements IAssetManagement {

    /** Converter lookup */
    private static IMongoConverterLookup LOOKUP = new MongoConverters();

    /** Injected with global SiteWhere Mongo client */
    private AssetManagementMongoClient mongoClient;

    public MongoAssetManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * @see com.sitewhere.mongodb.MongoTenantComponent#ensureIndexes()
     */
    @Override
    public void ensureIndexes() throws SiteWhereException {
	getMongoClient().getAssetTypesCollection().createIndex(new Document(MongoPersistentEntity.PROP_TOKEN, 1),
		new IndexOptions().unique(true).background(true));
	getMongoClient().getAssetsCollection().createIndex(new Document(MongoPersistentEntity.PROP_TOKEN, 1),
		new IndexOptions().unique(true).background(true));
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#createAssetType(com.sitewhere.spi.
     * asset.request.IAssetTypeCreateRequest)
     */
    @Override
    public IAssetType createAssetType(IAssetTypeCreateRequest request) throws SiteWhereException {
	// Use common logic so all backend implementations work the same.
	AssetType assetType = AssetManagementPersistence.assetTypeCreateLogic(request);

	MongoCollection<Document> types = getMongoClient().getAssetTypesCollection();
	Document created = MongoAssetType.toDocument(assetType);
	MongoPersistence.insert(types, created, ErrorCode.AssetTypeTokenInUse);

	return MongoAssetType.fromDocument(created);
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#updateAssetType(java.util.UUID,
     * com.sitewhere.spi.asset.request.IAssetTypeCreateRequest)
     */
    @Override
    public IAssetType updateAssetType(UUID assetTypeId, IAssetTypeCreateRequest request) throws SiteWhereException {
	// Get existing asset type.
	Document dbAsset = assertAssetTypeDocument(assetTypeId);
	AssetType assetType = MongoAssetType.fromDocument(dbAsset);

	// Use common logic so all backend implementations work the same.
	AssetManagementPersistence.assetTypeUpdateLogic(assetType, request);
	Document updated = MongoAssetType.toDocument(assetType);

	Document query = new Document(MongoPersistentEntity.PROP_ID, assetTypeId);
	MongoCollection<Document> types = getMongoClient().getAssetTypesCollection();
	MongoPersistence.update(types, query, updated);

	return MongoAssetType.fromDocument(updated);
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#getAssetType(java.util.UUID)
     */
    @Override
    public IAssetType getAssetType(UUID assetTypeId) throws SiteWhereException {
	Document dbAssetType = getAssetTypeDocument(assetTypeId);
	if (dbAssetType != null) {
	    return MongoAssetType.fromDocument(dbAssetType);
	}
	return null;
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#getAssetTypeByToken(java.lang.
     * String)
     */
    @Override
    public IAssetType getAssetTypeByToken(String token) throws SiteWhereException {
	try {
	    MongoCollection<Document> types = getMongoClient().getAssetTypesCollection();
	    Document query = new Document(MongoPersistentEntity.PROP_TOKEN, token);
	    Document dbAssetType = types.find(query).first();
	    if (dbAssetType != null) {
		return MongoAssetType.fromDocument(dbAssetType);
	    }
	    return null;
	} catch (MongoClientException e) {
	    throw MongoPersistence.handleClientException(e);
	}
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#deleteAssetType(java.util.UUID)
     */
    @Override
    public IAssetType deleteAssetType(UUID assetTypeId) throws SiteWhereException {
	Document existing = assertAssetTypeDocument(assetTypeId);

	AssetType assetType = MongoAssetType.fromDocument(existing);
	AssetManagementPersistence.assetTypeDeleteLogic(assetType, this);

	MongoCollection<Document> types = getMongoClient().getAssetTypesCollection();
	MongoPersistence.delete(types, existing);
	return MongoAssetType.fromDocument(existing);
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#listAssetTypes(com.sitewhere.spi.
     * search.area.IAssetTypeSearchCritiera)
     */
    @Override
    public ISearchResults<IAssetType> listAssetTypes(IAssetTypeSearchCritiera criteria) throws SiteWhereException {
	MongoCollection<Document> types = getMongoClient().getAssetTypesCollection();
	Document query = new Document();
	Document sort = new Document(MongoAssetType.PROP_NAME, 1);
	return MongoPersistence.search(IAssetType.class, types, query, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#createAsset(com.sitewhere.spi.asset.
     * request.IAssetCreateRequest)
     */
    @Override
    public IAsset createAsset(IAssetCreateRequest request) throws SiteWhereException {
	// Validate asset type.
	IAssetType assetType = getAssetTypeByToken(request.getAssetTypeToken());

	// Use common logic so all backend implementations work the same.
	Asset asset = AssetManagementPersistence.assetCreateLogic(assetType, request);

	MongoCollection<Document> assets = getMongoClient().getAssetsCollection();
	Document created = MongoAsset.toDocument(asset);
	MongoPersistence.insert(assets, created, ErrorCode.AssetTokenInUse);

	return MongoAsset.fromDocument(created);
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#updateAsset(java.util.UUID,
     * com.sitewhere.spi.asset.request.IAssetCreateRequest)
     */
    @Override
    public IAsset updateAsset(UUID assetId, IAssetCreateRequest request) throws SiteWhereException {
	// Validate asset type if updated.
	IAssetType assetType = (request.getAssetTypeToken() != null) ? getAssetTypeByToken(request.getAssetTypeToken())
		: null;

	// Get existing asset.
	Document dbAsset = assertAssetDocument(assetId);
	Asset asset = MongoAsset.fromDocument(dbAsset);

	// Use common logic so all backend implementations work the same.
	AssetManagementPersistence.assetUpdateLogic(assetType, asset, request);
	Document updated = MongoAsset.toDocument(asset);

	Document query = new Document(MongoPersistentEntity.PROP_ID, assetId);
	MongoCollection<Document> assets = getMongoClient().getAssetsCollection();
	MongoPersistence.update(assets, query, updated);

	return MongoAsset.fromDocument(updated);
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#getAsset(java.util.UUID)
     */
    @Override
    public IAsset getAsset(UUID assetId) throws SiteWhereException {
	Document dbAsset = getAssetDocument(assetId);
	if (dbAsset != null) {
	    return MongoAsset.fromDocument(dbAsset);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#getAssetByToken(java.lang.String)
     */
    @Override
    public IAsset getAssetByToken(String token) throws SiteWhereException {
	try {
	    MongoCollection<Document> assets = getMongoClient().getAssetsCollection();
	    Document query = new Document(MongoPersistentEntity.PROP_TOKEN, token);
	    Document dbAsset = assets.find(query).first();
	    if (dbAsset != null) {
		return MongoAsset.fromDocument(dbAsset);
	    }
	    return null;
	} catch (MongoClientException e) {
	    throw MongoPersistence.handleClientException(e);
	}
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetManagement#deleteAsset(java.util.UUID)
     */
    @Override
    public IAsset deleteAsset(UUID assetId) throws SiteWhereException {
	Document existing = assertAssetDocument(assetId);

	Asset asset = MongoAsset.fromDocument(existing);
	IDeviceManagement deviceManagement = ((IAssetManagementMicroservice) getTenantEngine().getMicroservice())
		.getDeviceManagementApiChannel();
	AssetManagementPersistence.assetDeleteLogic(asset, this, deviceManagement);

	MongoCollection<Document> assets = getMongoClient().getAssetsCollection();
	MongoPersistence.delete(assets, existing);
	return MongoAsset.fromDocument(existing);
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#listAssets(com.sitewhere.spi.search.
     * asset.IAssetSearchCriteria)
     */
    @Override
    public ISearchResults<IAsset> listAssets(IAssetSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> assets = getMongoClient().getAssetsCollection();
	Document query = new Document();

	// Add filter if asset type id specified.
	if (criteria.getAssetTypeToken() != null) {
	    IAssetType type = MongoAssetType.fromDocument(assertAssetType(criteria.getAssetTypeToken()));
	    query.append(MongoAsset.PROP_ASSET_TYPE_ID, type.getId());
	}

	Document sort = new Document(MongoAsset.PROP_NAME, 1);
	return MongoPersistence.search(IAsset.class, assets, query, sort, criteria, LOOKUP);
    }

    /**
     * Get the DBObject containing asset type information that matches the given
     * token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getAssetTypeDocumentByToken(String token) throws SiteWhereException {
	MongoCollection<Document> assetTypes = getMongoClient().getAssetTypesCollection();
	Document query = new Document(MongoPersistentEntity.PROP_TOKEN, token);
	return assetTypes.find(query).first();
    }

    /**
     * Return the {@link Document} for the asset type with the given token. Throws
     * an exception if the token is not found.
     * 
     * @param hardwareId
     * @return
     * @throws SiteWhereException
     */
    protected Document assertAssetType(String token) throws SiteWhereException {
	Document match = getAssetTypeDocumentByToken(token);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAssetTypeToken, ErrorLevel.INFO);
	}
	return match;
    }

    /**
     * Return the {@link Document} for the given asset type. Returns null if not
     * found.
     * 
     * @param assetTypeId
     * @return
     * @throws SiteWhereException
     */
    protected Document getAssetTypeDocument(UUID assetTypeId) throws SiteWhereException {
	try {
	    MongoCollection<Document> types = getMongoClient().getAssetTypesCollection();
	    Document query = new Document(MongoPersistentEntity.PROP_ID, assetTypeId);
	    return types.find(query).first();
	} catch (MongoClientException e) {
	    throw MongoPersistence.handleClientException(e);
	}
    }

    /**
     * Return the {@link Document} for the asset type with the given id. Throws an
     * exception if the token is not valid.
     * 
     * @param assetId
     * @return
     * @throws SiteWhereException
     */
    protected Document assertAssetTypeDocument(UUID assetId) throws SiteWhereException {
	Document match = getAssetTypeDocument(assetId);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAssetTypeId, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Return the {@link Document} for the given asset. Returns null if not found.
     * 
     * @param assetId
     * @return
     * @throws SiteWhereException
     */
    protected Document getAssetDocument(UUID assetId) throws SiteWhereException {
	try {
	    MongoCollection<Document> assets = getMongoClient().getAssetsCollection();
	    Document query = new Document(MongoPersistentEntity.PROP_ID, assetId);
	    return assets.find(query).first();
	} catch (MongoClientException e) {
	    throw MongoPersistence.handleClientException(e);
	}
    }

    /**
     * Return the {@link Document} for the asset with the given id. Throws an
     * exception if the token is not valid.
     * 
     * @param assetId
     * @return
     * @throws SiteWhereException
     */
    protected Document assertAssetDocument(UUID assetId) throws SiteWhereException {
	Document match = getAssetDocument(assetId);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAssetId, ErrorLevel.ERROR);
	}
	return match;
    }

    /*
     * @see com.sitewhere.mongodb.MongoTenantComponent#getMongoClient()
     */
    @Override
    public AssetManagementMongoClient getMongoClient() {
	return mongoClient;
    }

    public void setMongoClient(AssetManagementMongoClient mongoClient) {
	this.mongoClient = mongoClient;
    }
}