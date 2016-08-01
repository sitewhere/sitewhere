/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.asset.filesystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.rest.model.command.CommandResponse;
import com.sitewhere.server.asset.AssetMatcher;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.command.CommandResult;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Base class for asset modules that load asset information from the filesystem.
 * 
 * @author Derek
 *
 * @param <T>
 */
public abstract class FileSystemAssetModule<T extends Asset> extends LifecycleComponent implements IAssetModule<T> {

	/** Serial version UID */
	private static final long serialVersionUID = 8266923437767568336L;

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(FileSystemAssetModule.class);

	/** Map of assets by unique id */
	protected Map<String, T> assetsById;

	/** Matcher used for searches */
	protected AssetMatcher matcher = new AssetMatcher();

	/** Filename used to load assets */
	private String filename;

	/** Module id */
	private String moduleId;

	/** Module name */
	private String moduleName;

	public FileSystemAssetModule(String filename, String moduleId, String moduleName) {
		super(LifecycleComponentType.AssetModule);
		this.filename = filename;
		this.moduleId = moduleId;
		this.moduleName = moduleName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	public void start() throws SiteWhereException {
		reload();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	public void stop() throws SiteWhereException {
	}

	/**
	 * Reloads list of person assets from the filesystem.
	 */
	protected void reload() throws SiteWhereException {
		IResource configResource = SiteWhere.getServer().getConfigurationResolver().getAssetResource(getFilename());
		LOGGER.info("Loading assets from: " + getFilename());

		// Unmarshal assets from XML file and store in data object.
		List<T> assets = unmarshal(configResource);
		this.assetsById = new HashMap<String, T>();
		for (T asset : assets) {
			assetsById.put(asset.getId(), asset);
		}
		showLoadResults();
	}

	/**
	 * Log the number of assets loaded for each type.
	 */
	protected void showLoadResults() {
		String message = "Loaded " + assetsById.size() + " assets.";
		LOGGER.info(message);
	}

	/**
	 * Implemented in subclasses to unmarshal file into assets.
	 * 
	 * @param file
	 * @return
	 * @throws SiteWhereException
	 */
	protected abstract List<T> unmarshal(IResource resource) throws SiteWhereException;

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
	 * @see com.sitewhere.spi.asset.IAssetModule#getId()
	 */
	public String getId() {
		return getModuleId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getName()
	 */
	public String getName() {
		return getModuleName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.server.lifecycle.LifecycleComponent#getComponentName()
	 */
	public String getComponentName() {
		return getClass().getSimpleName() + " [" + getId() + "] " + getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getAssetById(java.lang.String)
	 */
	public T getAssetById(String id) throws SiteWhereException {
		return assetsById.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#search(java.lang.String)
	 */
	public List<T> search(String criteria) throws SiteWhereException {
		criteria = criteria.toLowerCase();
		List<T> results = new ArrayList<T>();
		if (criteria.length() == 0) {
			results.addAll(assetsById.values());
			return results;
		}
		for (T asset : assetsById.values()) {
			if (matcher.isMatch(getAssetType(), asset, criteria)) {
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
	public ICommandResponse refresh() throws SiteWhereException {
		try {
			reload();
			showLoadResults();
			return new CommandResponse(CommandResult.Successful, "Refresh successful.");
		} catch (SiteWhereException e) {
			return new CommandResponse(CommandResult.Failed, e.getMessage());
		}
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
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
}