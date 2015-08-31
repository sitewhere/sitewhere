/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.asset;

import com.sitewhere.SiteWhere;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.asset.IHardwareAsset;
import com.sitewhere.spi.asset.ILocationAsset;
import com.sitewhere.spi.asset.IPersonAsset;
import com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest;
import com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest;
import com.sitewhere.spi.asset.request.ILocationAssetCreateRequest;
import com.sitewhere.spi.asset.request.IPersonAssetCreateRequest;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Trigger actions based on asset management API calls.
 * 
 * @author Derek
 */
public class AssetManagementTriggers extends AssetManagementDecorator {

	public AssetManagementTriggers(IAssetManagement delegate) {
		super(delegate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.server.asset.AssetManagementDecorator#createAssetCategory(com.sitewhere
	 * .spi.asset.request.IAssetCategoryCreateRequest)
	 */
	@Override
	public IAssetCategory createAssetCategory(IAssetCategoryCreateRequest request) throws SiteWhereException {
		IAssetCategory created = super.createAssetCategory(request);
		refreshAll();
		return created;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.server.asset.AssetManagementDecorator#updateAssetCategory(java.lang
	 * .String, com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest)
	 */
	@Override
	public IAssetCategory updateAssetCategory(String categoryId, IAssetCategoryCreateRequest request)
			throws SiteWhereException {
		IAssetCategory updated = super.updateAssetCategory(categoryId, request);
		refreshAll();
		return updated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.server.asset.AssetManagementDecorator#deleteAssetCategory(java.lang
	 * .String)
	 */
	@Override
	public IAssetCategory deleteAssetCategory(String categoryId) throws SiteWhereException {
		IAssetCategory deleted = super.deleteAssetCategory(categoryId);
		refreshAll();
		return deleted;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.server.asset.AssetManagementDecorator#createPersonAsset(java.lang
	 * .String, com.sitewhere.spi.asset.request.IPersonAssetCreateRequest)
	 */
	@Override
	public IPersonAsset createPersonAsset(String categoryId, IPersonAssetCreateRequest request)
			throws SiteWhereException {
		IPersonAsset asset = super.createPersonAsset(categoryId, request);
		refreshModule(categoryId);
		return asset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.server.asset.AssetManagementDecorator#updatePersonAsset(java.lang
	 * .String, java.lang.String,
	 * com.sitewhere.spi.asset.request.IPersonAssetCreateRequest)
	 */
	@Override
	public IPersonAsset updatePersonAsset(String categoryId, String assetId, IPersonAssetCreateRequest request)
			throws SiteWhereException {
		IPersonAsset asset = super.updatePersonAsset(categoryId, assetId, request);
		refreshModule(categoryId);
		return asset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.server.asset.AssetManagementDecorator#createHardwareAsset(java.lang
	 * .String, com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest)
	 */
	@Override
	public IHardwareAsset createHardwareAsset(String categoryId, IHardwareAssetCreateRequest request)
			throws SiteWhereException {
		IHardwareAsset asset = super.createHardwareAsset(categoryId, request);
		refreshModule(categoryId);
		return asset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.server.asset.AssetManagementDecorator#updateHardwareAsset(java.lang
	 * .String, java.lang.String,
	 * com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest)
	 */
	@Override
	public IHardwareAsset updateHardwareAsset(String categoryId, String assetId,
			IHardwareAssetCreateRequest request) throws SiteWhereException {
		IHardwareAsset asset = super.updateHardwareAsset(categoryId, assetId, request);
		refreshModule(categoryId);
		return asset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.server.asset.AssetManagementDecorator#createLocationAsset(java.lang
	 * .String, com.sitewhere.spi.asset.request.ILocationAssetCreateRequest)
	 */
	@Override
	public ILocationAsset createLocationAsset(String categoryId, ILocationAssetCreateRequest request)
			throws SiteWhereException {
		ILocationAsset asset = super.createLocationAsset(categoryId, request);
		refreshModule(categoryId);
		return asset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.server.asset.AssetManagementDecorator#updateLocationAsset(java.lang
	 * .String, java.lang.String,
	 * com.sitewhere.spi.asset.request.ILocationAssetCreateRequest)
	 */
	@Override
	public ILocationAsset updateLocationAsset(String categoryId, String assetId,
			ILocationAssetCreateRequest request) throws SiteWhereException {
		ILocationAsset asset = super.updateLocationAsset(categoryId, assetId, request);
		refreshModule(categoryId);
		return asset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.server.asset.AssetManagementDecorator#deleteAsset(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public IAsset deleteAsset(String categoryId, String assetId) throws SiteWhereException {
		IAsset asset = super.deleteAsset(categoryId, assetId);
		refreshModule(categoryId);
		return asset;
	}

	/**
	 * Refresh all datastore modules.
	 * 
	 * @throws SiteWhereException
	 */
	protected void refreshAll() throws SiteWhereException {
		IAssetModuleManager manager = SiteWhere.getServer().getAssetModuleManager(getTenant());
		if (manager.getLifecycleStatus() == LifecycleStatus.Started) {
			manager.refreshDatastoreModules();
		}
	}

	/**
	 * Refresh an asset module.
	 * 
	 * @param id
	 * @throws SiteWhereException
	 */
	protected void refreshModule(String id) throws SiteWhereException {
		IAssetModuleManager manager = SiteWhere.getServer().getAssetModuleManager(getTenant());
		if (manager.getLifecycleStatus() == LifecycleStatus.Started) {
			IAssetModule<?> module = SiteWhere.getServer().getAssetModuleManager(getTenant()).getModule(id);
			if (module != null) {
				module.refresh();
			}
		}
	}
}