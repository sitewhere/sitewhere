/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.asset.datastore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.rest.model.command.CommandResponse;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.server.asset.AssetMatcher;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.command.CommandResult;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IAssetModule} that loads assets from the datastore.
 * 
 * @author Derek
 */
public class DataStoreAssetModule<T extends IAsset> extends TenantLifecycleComponent {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DataStoreAssetModule.class);

	/** Asset category */
	private IAssetCategory category;

	/** Cache of assets for category */
	protected Map<String, T> assetCache = new HashMap<String, T>();

	/** Matcher used for searches */
	protected AssetMatcher matcher = new AssetMatcher();

	public DataStoreAssetModule(IAssetCategory category) {
		super(LifecycleComponentType.AssetModule);
		this.category = category;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		doLoadAssets();
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
	 * @see com.sitewhere.server.lifecycle.LifecycleComponent#getComponentName()
	 */
	@Override
	public String getComponentName() {
		return getClass().getSimpleName() + " [" + getCategory().getId() + "] " + getCategory().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetCategory#getId()
	 */
	public String getId() {
		return getCategory().getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetCategory#getName()
	 */
	public String getName() {
		return getCategory().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetCategory#getAssetType()
	 */
	public AssetType getAssetType() {
		return getCategory().getAssetType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#refresh()
	 */
	public ICommandResponse refresh() throws SiteWhereException {
		return doLoadAssets();
	}

	/**
	 * Load the list of assets for the category.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected ICommandResponse doLoadAssets() {
		try {
			ISearchResults<IAsset> assets =
					SiteWhere.getServer().getAssetManagement(getTenant()).listAssets(category.getId(),
							new SearchCriteria(1, 0));
			assetCache.clear();
			for (IAsset asset : assets.getResults()) {
				assetCache.put(asset.getId(), (T) asset);
			}
			return new CommandResponse(CommandResult.Successful, "Asset module refreshed.");
		} catch (SiteWhereException e) {
			return new CommandResponse(CommandResult.Failed,
					"Asset module refreshed failed. " + e.getMessage());
		}
	}

	/**
	 * Get an asset by unique id.
	 * 
	 * @param id
	 * @return
	 */
	protected T doGetAsset(String id) {
		return assetCache.get(id);
	}

	/**
	 * Search cached assets based on criteria.
	 * 
	 * @param criteria
	 * @return
	 */
	protected List<T> doSearch(String criteria) {
		List<T> results = new ArrayList<T>();
		for (T asset : assetCache.values()) {
			if (matcher.isMatch(getCategory().getAssetType(), asset, criteria)) {
				results.add(asset);
			}
		}
		return results;
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

	public IAssetCategory getCategory() {
		return category;
	}

	public void setCategory(IAssetCategory category) {
		this.category = category;
	}
}