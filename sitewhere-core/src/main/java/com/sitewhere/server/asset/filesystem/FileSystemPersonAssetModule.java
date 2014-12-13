/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.asset.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sitewhere.configuration.TomcatConfigurationResolver;
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.rest.model.command.CommandResponse;
import com.sitewhere.server.asset.AssetMatcher;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.command.CommandResult;
import com.sitewhere.spi.command.ICommandResponse;

/**
 * Modules that loads a list of person assets from an XML file on the filesystem.
 * 
 * @author Derek Adams
 */
public class FileSystemPersonAssetModule extends LifecycleComponent implements IAssetModule<PersonAsset> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(FileSystemPersonAssetModule.class);

	/** Module id */
	public static final String MODULE_ID = "fs-persons";

	/** Module name */
	public static final String MODULE_NAME = "Default Identity Management";

	/** Filename in SiteWhere config folder that contains person assets */
	public static final String PERSON_CONFIG_FILENAME = "person-assets.xml";

	/** Map of assets by unique id */
	protected Map<String, PersonAsset> assetsById;

	/** Matcher used for searches */
	protected AssetMatcher matcher = new AssetMatcher();

	/** Filename used to load assets */
	private String filename = PERSON_CONFIG_FILENAME;

	/** Module id */
	private String moduleId = MODULE_ID;

	/** Module name */
	private String moduleName = MODULE_NAME;

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
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	/**
	 * Reloads list of person assets from the filesystem.
	 */
	protected void reload() throws SiteWhereException {
		File config = TomcatConfigurationResolver.getSiteWhereConfigFolder();
		File assetsFolder = new File(config, IFileSystemAssetModuleConstants.ASSETS_FOLDER);
		if (!assetsFolder.exists()) {
			throw new SiteWhereException("Assets subfolder not found. Looking for: "
					+ assetsFolder.getAbsolutePath());
		}
		File personConfig = new File(assetsFolder, getFilename());
		if (!personConfig.exists()) {
			throw new SiteWhereException("Person assets file missing. Looking for: "
					+ personConfig.getAbsolutePath());
		}
		LOGGER.info("Loading person assets from: " + personConfig.getAbsolutePath());

		// Unmarshal assets from XML file and store in data object.
		List<PersonAsset> assets = MarshalUtils.loadPersonAssets(personConfig);
		this.assetsById = new HashMap<String, PersonAsset>();
		for (PersonAsset asset : assets) {
			assetsById.put(asset.getId(), asset);
		}
		showLoadResults();
	}

	/**
	 * Log the number of assets loaded for each type.
	 */
	protected void showLoadResults() {
		String message = "Loaded " + assetsById.size() + " person assets.";
		LOGGER.info(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	public void stop() throws SiteWhereException {
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
	 * @see com.sitewhere.spi.asset.IAssetModule#getAssetType()
	 */
	public AssetType getAssetType() {
		return AssetType.Person;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getAssetById(java.lang.String)
	 */
	public PersonAsset getAssetById(String id) throws SiteWhereException {
		return assetsById.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#search(java.lang.String)
	 */
	public List<PersonAsset> search(String criteria) throws SiteWhereException {
		criteria = criteria.toLowerCase();
		List<PersonAsset> results = new ArrayList<PersonAsset>();
		if (criteria.length() == 0) {
			results.addAll(assetsById.values());
			return results;
		}
		for (PersonAsset asset : assetsById.values()) {
			if (matcher.isPersonMatch(asset, criteria)) {
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