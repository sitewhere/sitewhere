/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.rest.model.command.CommandResponse;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.server.asset.AssetMatcher;
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
public class FileSystemPersonAssetModule implements IAssetModule<PersonAsset> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(FileSystemPersonAssetModule.class);

	/** Module id */
	public static final String MODULE_ID = "filesystem-person";

	/** Module name */
	public static final String MODULE_NAME = "Filesystem Person Asset Module";

	/** Filename in SiteWhere config folder that contains person assets */
	public static final String PERSON_CONFIG_FILENAME = "person-assets.xml";

	/** Map of assets by unique id */
	protected Map<String, PersonAsset> assetsById;

	/** Matcher used for searches */
	protected AssetMatcher matcher = new AssetMatcher();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#start()
	 */
	public void start() throws SiteWhereException {
		reload();
	}

	/**
	 * Reloads list of person assets from the filesystem.
	 */
	protected void reload() throws SiteWhereException {
		File config = SiteWhereServer.getSiteWhereConfigFolder();
		File assetsFolder = new File(config, IFileSystemAssetModuleConstants.ASSETS_FOLDER);
		if (!assetsFolder.exists()) {
			throw new SiteWhereException("Assets subfolder not found. Looking for: "
					+ assetsFolder.getAbsolutePath());
		}
		File personConfig = new File(assetsFolder, PERSON_CONFIG_FILENAME);
		if (!personConfig.exists()) {
			throw new SiteWhereException("Person assets file missing. Looking for: "
					+ personConfig.getAbsolutePath());
		}
		LOGGER.info("Loading person assets from: " + personConfig.getAbsolutePath());

		// Unmarshal assets from XML file and store in data object.
		List<PersonAsset> assets = new ArrayList<PersonAsset>();
		Map<String, PersonAsset> assetsById = new HashMap<String, PersonAsset>();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(FileSystemPersonAssets.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			FileSystemPersonAssets xmlAssets = (FileSystemPersonAssets) jaxbUnmarshaller
					.unmarshal(personConfig);
			for (FileSystemPersonAsset xmlAsset : xmlAssets.getPersonAssets()) {
				PersonAsset asset = new PersonAsset();
				asset.setId(xmlAsset.getId());
				asset.setName(xmlAsset.getName());
				asset.setUserName(xmlAsset.getUserName());
				asset.setEmailAddress(xmlAsset.getEmailAddress());
				asset.setPhotoUrl(xmlAsset.getPhotoUrl());
				for (FileSystemAssetProperty xmlProperty : xmlAsset.getProperties()) {
					asset.setProperty(xmlProperty.getName(), xmlProperty.getValue());
				}
				if (xmlAsset.getRoles() != null) {
					List<String> roles = xmlAsset.getRoles().getRoles();
					for (String role : roles) {
						asset.getRoles().add(role);
					}
				}
				assets.add(asset);
				assetsById.put(asset.getId(), asset);
			}
			this.assetsById = assetsById;
			showLoadResults();
		} catch (Exception e) {
			throw new SiteWhereException("Unable to unmarshal person assets file.", e);
		}
	}

	/**
	 * Log the number of assets loaded for each type.
	 */
	protected void showLoadResults() {
		String message = "Loaded " + assetsById.size() + " assets.";
		LOGGER.info(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#stop()
	 */
	public void stop() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getId()
	 */
	public String getId() {
		return MODULE_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getName()
	 */
	public String getName() {
		return MODULE_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.asset.IAssetModule#isAssetTypeSupported(com.sitewhere.spi.asset
	 * .AssetType)
	 */
	public boolean isAssetTypeSupported(AssetType type) {
		if (type == AssetType.Person) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.asset.IAssetModule#getAssetById(com.sitewhere.spi.asset.AssetType
	 * , java.lang.String)
	 */
	public PersonAsset getAssetById(AssetType type, String id) throws SiteWhereException {
		return assetsById.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#search(com.sitewhere.spi.asset.AssetType,
	 * java.lang.String)
	 */
	public List<PersonAsset> search(AssetType type, String criteria) throws SiteWhereException {
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
}