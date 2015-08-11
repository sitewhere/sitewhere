/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.asset;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest;
import com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest;
import com.sitewhere.spi.asset.request.ILocationAssetCreateRequest;
import com.sitewhere.spi.asset.request.IPersonAssetCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Asset management interface implemented by datastores that can store assets.
 * 
 * @author Derek
 */
public interface IAssetManagement extends ILifecycleComponent {

	/**
	 * Create a new asset category.
	 * 
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IAssetCategory createAssetCategory(IAssetCategoryCreateRequest request) throws SiteWhereException;

	/**
	 * Get an asset category by id.
	 * 
	 * @param categoryId
	 * @return
	 * @throws SiteWhereException
	 */
	public IAssetCategory getAssetCategory(String categoryId) throws SiteWhereException;

	/**
	 * List asset categories.
	 * 
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IAssetCategory> listAssetCategories(ISearchCriteria criteria)
			throws SiteWhereException;

	/**
	 * Delete an asset category and all contained assets.
	 * 
	 * @param categoryId
	 * @return
	 * @throws SiteWhereException
	 */
	public IAssetCategory deleteAssetCategory(String categoryId) throws SiteWhereException;

	/**
	 * Create a new {@link IPersonAsset}.
	 * 
	 * @param categoryId
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IPersonAsset createPersonAsset(String categoryId, IPersonAssetCreateRequest request)
			throws SiteWhereException;

	/**
	 * Create a new {@link IHardwareAsset}.
	 * 
	 * @param categoryId
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IHardwareAsset createHardwareAsset(String categoryId, IHardwareAssetCreateRequest request)
			throws SiteWhereException;

	/**
	 * Create a new {@link ILocationAsset}.
	 * 
	 * @param categoryId
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public ILocationAsset createLocationAsset(String categoryId, ILocationAssetCreateRequest request)
			throws SiteWhereException;

	/**
	 * Get the asset for the given id in the given category.
	 * 
	 * @param categoryId
	 * @param assetId
	 * @return
	 * @throws SiteWhereException
	 */
	public IAsset getAsset(String categoryId, String assetId) throws SiteWhereException;

	/**
	 * Delete the asset with the given id in the given category.
	 * 
	 * @param categoryId
	 * @param assetId
	 * @return
	 * @throws SiteWhereException
	 */
	public IAsset deleteAsset(String categoryId, String assetId) throws SiteWhereException;

	/**
	 * List assets in a category that meet the given criteria.
	 * 
	 * @param categoryId
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IAsset> listAssets(String categoryId, ISearchCriteria criteria)
			throws SiteWhereException;
}