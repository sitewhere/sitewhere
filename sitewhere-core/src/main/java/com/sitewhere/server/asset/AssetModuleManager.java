/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.asset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.command.ICommandResponse;

/**
 * Manages the list of modules
 * 
 * @author dadams
 */
public class AssetModuleManager extends LifecycleComponent implements IAssetModuleManager {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(AssetModuleManager.class);

	/** List of asset modules */
	private List<IAssetModule<?>> modules;

	/** Map of asset modules by unique id */
	private Map<String, IAssetModule<?>> modulesById = new HashMap<String, IAssetModule<?>>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	public void start() throws SiteWhereException {
		modulesById.clear();
		for (IAssetModule<?> module : modules) {
			startNestedComponent(module, true);
			modulesById.put(module.getId(), module);
		}
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
	public void stop() {
		for (IAssetModule<?> module : modules) {
			module.lifecycleStop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModuleManager#getAssetById(java.lang.String,
	 * java.lang.String)
	 */
	public IAsset getAssetById(String assetModuleId, String id) throws SiteWhereException {
		IAssetModule<?> match = assertAssetModule(assetModuleId);
		return match.getAssetById(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModuleManager#search(java.lang.String,
	 * java.lang.String)
	 */
	public List<? extends IAsset> search(String assetModuleId, String criteria) throws SiteWhereException {
		IAssetModule<?> match = assertAssetModule(assetModuleId);
		List<? extends IAsset> results = match.search(criteria);
		Collections.sort(results);
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModuleManager#refreshModules()
	 */
	public List<ICommandResponse> refreshModules() throws SiteWhereException {
		List<ICommandResponse> responses = new ArrayList<ICommandResponse>();
		for (IAssetModule<?> module : modules) {
			responses.add(module.refresh());
		}
		return responses;
	}

	/**
	 * Get asset module by id or throw exception if not found.
	 * 
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	protected IAssetModule<?> assertAssetModule(String id) throws SiteWhereException {
		IAssetModule<?> match = modulesById.get(id);
		if (match == null) {
			throw new SiteWhereException("Invalid asset module id: " + id);
		}
		return match;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModuleManager#getModules()
	 */
	public List<IAssetModule<?>> getModules() {
		return modules;
	}

	public void setModules(List<IAssetModule<?>> modules) {
		this.modules = modules;
	}
}