/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.asset.persistence;

import java.util.Collections;

import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.persistence.Persistence;
import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.rest.model.asset.AssetType;
import com.sitewhere.rest.model.search.asset.AssetSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceAssignmentSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.asset.request.IAssetCreateRequest;
import com.sitewhere.spi.asset.request.IAssetTypeCreateRequest;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Persistence logic for asset management components.
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
	Persistence.brandedEntityCreateLogic(request, type);

	require("Name", request.getName());
	type.setName(request.getName());
	type.setDescription(request.getDescription());
	type.setAssetCategory(request.getAssetCategory());

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
	Persistence.brandedEntityUpdateLogic(request, target);

	if (request.getName() != null) {
	    target.setName(request.getName());
	}
	if (request.getDescription() != null) {
	    target.setDescription(request.getDescription());
	}
	if (request.getAssetCategory() != null) {
	    target.setAssetCategory(request.getAssetCategory());
	}
    }

    /**
     * Common logic for deleting an asset type.
     * 
     * @param assetType
     * @param assetManagement
     * @throws SiteWhereException
     */
    public static void assetTypeDeleteLogic(IAssetType assetType, IAssetManagement assetManagement)
	    throws SiteWhereException {
	AssetSearchCriteria criteria = new AssetSearchCriteria(1, 1);
	criteria.setAssetTypeToken(assetType.getToken());
	ISearchResults<? extends IAsset> assets = assetManagement.listAssets(criteria);
	if (assets.getNumResults() > 0) {
	    throw new SiteWhereSystemException(ErrorCode.AssetTypeNoDeleteHasAssets, ErrorLevel.ERROR);
	}
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
	Persistence.brandedEntityCreateLogic(request, asset);

	require("Name", request.getName());
	asset.setName(request.getName());
	asset.setAssetTypeId(assetType.getId());

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
	Persistence.brandedEntityUpdateLogic(request, target);

	if (request.getName() != null) {
	    target.setName(request.getName());
	}
	if (request.getAssetTypeToken() != null) {
	    target.setAssetTypeId(assetType.getId());
	}
    }

    /**
     * Common logic for deleting an asset.
     * 
     * @param asset
     * @param assetManagement
     * @param deviceManagement
     * @throws SiteWhereException
     */
    public static void assetDeleteLogic(IAsset asset, IAssetManagement assetManagement,
	    IDeviceManagement deviceManagement) throws SiteWhereException {
	DeviceAssignmentSearchCriteria criteria = new DeviceAssignmentSearchCriteria(1, 1);
	criteria.setAssetTokens(Collections.singletonList(asset.getToken()));

	ISearchResults<? extends IDeviceAssignment> assignments = deviceManagement.listDeviceAssignments(criteria);
	if (assignments.getNumResults() > 0) {
	    throw new SiteWhereSystemException(ErrorCode.AssetNoDeleteHasAssignments, ErrorLevel.ERROR);
	}
    }
}