/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.gnuhealth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sitewhere.rest.model.asset.LocationAsset;
import com.sitewhere.server.asset.AssetMatcher;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Asset module that enumerates wards from GNU Health for use as SiteWhere assets.
 * 
 * @author Derek
 */
public class GnuHealthWardsAssetModule extends LifecycleComponent implements IAssetModule<LocationAsset> {

	/** Serial version UID */
	private static final long serialVersionUID = 5506023623346185300L;

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(GnuHealthWardsAssetModule.class);

	/** Module id */
	private static final String MODULE_ID = "gnuhealth-wards";

	/** Module name */
	private static final String MODULE_NAME = "GNU Health - Wards";

	/** Unique module id */
	private String moduleId = MODULE_ID;

	/** Module name */
	private String moduleName = MODULE_NAME;

	/** Common GNU Health configuration */
	private GnuHealthConfiguration configuration;

	/** Used to find search results */
	private AssetMatcher matcher = new AssetMatcher();

	public GnuHealthWardsAssetModule() {
		super(LifecycleComponentType.AssetModule);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		// Trigger data caching if not already done.
		getConfiguration().getGnuHealthData();
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
	 * @see com.sitewhere.spi.asset.IAssetModule#getAssetById(java.lang.String)
	 */
	@Override
	public LocationAsset getAssetById(String id) throws SiteWhereException {
		return getConfiguration().getGnuHealthData().getCachedWards().get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#search(java.lang.String)
	 */
	@Override
	public List<LocationAsset> search(String criteria) throws SiteWhereException {
		criteria = criteria.toLowerCase();
		List<LocationAsset> results = new ArrayList<LocationAsset>();
		Map<String, LocationAsset> cache = getConfiguration().getGnuHealthData().getCachedWards();
		if (criteria.length() == 0) {
			results.addAll(cache.values());
			return results;
		}
		for (LocationAsset asset : cache.values()) {
			if (matcher.isLocationMatch(asset, criteria)) {
				results.add(asset);
			}
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#refresh()
	 */
	@Override
	public ICommandResponse refresh() throws SiteWhereException {
		return getConfiguration().getGnuHealthData().refreshWards();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getId()
	 */
	@Override
	public String getId() {
		return getModuleId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getName()
	 */
	@Override
	public String getName() {
		return getModuleName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getAssetType()
	 */
	@Override
	public AssetType getAssetType() {
		return AssetType.Location;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public GnuHealthConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(GnuHealthConfiguration configuration) {
		this.configuration = configuration;
	}
}