/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.persistence;

import java.util.UUID;

import com.sitewhere.persistence.Persistence;
import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.rest.model.asset.AssetType;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.asset.request.IAssetCreateRequest;
import com.sitewhere.spi.asset.request.IAssetTypeCreateRequest;

/**
 * Persistence logic for asset management components.
 * 
 * @author Derek
 */
public class AssetManagementPersistence extends Persistence {

    /**
     * Handle base logic for creating an asset type.
     * 
     * @param assetType
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static AssetType assetTypeCreateLogic(IAssetTypeCreateRequest request) throws SiteWhereException {
	AssetType type = new AssetType();
	type.setId(UUID.randomUUID());
	type.setDescription(request.getDescription());

	// Unique token is required.
	require("Token", request.getToken());
	type.setToken(request.getToken());

	require("Name", request.getName());
	type.setName(request.getName());

	require("Image URL", request.getImageUrl());
	type.setImageUrl(request.getImageUrl());

	MetadataProvider.copy(request.getMetadata(), type);
	AssetManagementPersistence.initializeEntityMetadata(type);

	return type;
    }

    /**
     * Handle common asset type create logic.
     * 
     * @param target
     * @param request
     * @throws SiteWhereException
     */
    public static void assetTypeUpdateLogic(AssetType target, IAssetTypeCreateRequest request)
	    throws SiteWhereException {
	if (request.getToken() != null) {
	    target.setToken(request.getToken());
	}
	if (request.getName() != null) {
	    target.setName(request.getName());
	}
	if (request.getDescription() != null) {
	    target.setDescription(request.getDescription());
	}
	if (request.getImageUrl() != null) {
	    target.setImageUrl(request.getImageUrl());
	}
	if (request.getMetadata() != null) {
	    target.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), target);
	}
	AssetManagementPersistence.setUpdatedEntityMetadata(target);
    }

    /**
     * Handle base logic for creating an asset.
     * 
     * @param assetType
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static Asset assetCreateLogic(IAssetType assetType, IAssetCreateRequest request) throws SiteWhereException {
	Asset asset = new Asset();
	asset.setId(UUID.randomUUID());
	asset.setAssetTypeId(assetType.getId());

	// Unique token is required.
	require("Token", request.getToken());
	asset.setToken(request.getToken());

	require("Name", request.getName());
	asset.setName(request.getName());

	require("Image URL", request.getImageUrl());
	asset.setImageUrl(request.getImageUrl());

	MetadataProvider.copy(request.getMetadata(), asset);
	AssetManagementPersistence.initializeEntityMetadata(asset);

	return asset;
    }

    /**
     * Handle base logic for updating an asset.
     * 
     * @param assetType
     * @param target
     * @param request
     * @throws SiteWhereException
     */
    public static void assetUpdateLogic(IAssetType assetType, Asset target, IAssetCreateRequest request)
	    throws SiteWhereException {
	if (request.getAssetTypeToken() != null) {
	    target.setAssetTypeId(assetType.getId());
	}
	if (request.getToken() != null) {
	    target.setToken(request.getToken());
	}
	if (request.getName() != null) {
	    target.setName(request.getName());
	}
	if (request.getImageUrl() != null) {
	    target.setImageUrl(request.getImageUrl());
	}
	if (request.getMetadata() != null) {
	    target.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), target);
	}
	AssetManagementPersistence.setUpdatedEntityMetadata(target);
    }
}