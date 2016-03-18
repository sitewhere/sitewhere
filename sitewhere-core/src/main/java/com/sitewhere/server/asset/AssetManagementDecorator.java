/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.asset;

import com.sitewhere.server.lifecycle.LifecycleComponentDecorator;
import com.sitewhere.spi.SiteWhereException;
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
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Wraps an asset management implementation. Subclasses can implement only the methods
 * they need to override.
 * 
 * @author Derek
 */
public class AssetManagementDecorator extends LifecycleComponentDecorator implements IAssetManagement {

	/** Delegate */
	private IAssetManagement delegate;

	public AssetManagementDecorator(IAssetManagement delegate) {
		super(delegate);
		this.delegate = delegate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent#setTenant(com.sitewhere
	 * .spi.user.ITenant)
	 */
	@Override
	public void setTenant(ITenant tenant) {
		delegate.setTenant(tenant);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent#getTenant()
	 */
	@Override
	public ITenant getTenant() {
		return delegate.getTenant();
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
		return delegate.createAssetCategory(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetManagement#getAssetCategory(java.lang.String)
	 */
	@Override
	public IAssetCategory getAssetCategory(String categoryId) throws SiteWhereException {
		return delegate.getAssetCategory(categoryId);
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
		return delegate.updateAssetCategory(categoryId, request);
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
		return delegate.listAssetCategories(criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetManagement#deleteAssetCategory(java.lang.String)
	 */
	@Override
	public IAssetCategory deleteAssetCategory(String categoryId) throws SiteWhereException {
		return delegate.deleteAssetCategory(categoryId);
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
		return delegate.createPersonAsset(categoryId, request);
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
		return delegate.updatePersonAsset(categoryId, assetId, request);
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
		return delegate.createHardwareAsset(categoryId, request);
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
		return delegate.updateHardwareAsset(categoryId, assetId, request);
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
		return delegate.createLocationAsset(categoryId, request);
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
		return delegate.updateLocationAsset(categoryId, assetId, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetManagement#getAsset(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public IAsset getAsset(String categoryId, String assetId) throws SiteWhereException {
		return delegate.getAsset(categoryId, assetId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetManagement#deleteAsset(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public IAsset deleteAsset(String categoryId, String assetId) throws SiteWhereException {
		return delegate.deleteAsset(categoryId, assetId);
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
		return delegate.listAssets(categoryId, criteria);
	}
}