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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.command.CommandResponse;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.server.asset.AssetMatcher;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.command.CommandResult;
import com.sitewhere.spi.command.ICommandResponse;

/**
 * Modules that loads a list of hardware assets from an XML file on the filesystem.
 * 
 * @author Derek Adams
 */
public class FileSystemHardwareAssetModule implements IAssetModule<HardwareAsset> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(FileSystemHardwareAssetModule.class);

	/** Module id */
	public static final String MODULE_ID = "filesystem-hardware";

	/** Module name */
	public static final String MODULE_NAME = "Filesystem Hardware Asset Module";

	/** Filename in SiteWhere config folder that contains hardware assets */
	public static final String HARDWARE_CONFIG_FILENAME = "hardware-assets.xml";

	/** Map of device assets by unique id */
	protected Map<String, HardwareAsset> deviceAssetsById;

	/** Map of hardware assets by unique id */
	protected Map<String, HardwareAsset> hardwareAssetsById;

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
	 * Reloads list of hardware assets from the filesystem.
	 */
	protected void reload() throws SiteWhereException {
		File config = SiteWhereServer.getSiteWhereConfigFolder();
		File assetsFolder = new File(config, IFileSystemAssetModuleConstants.ASSETS_FOLDER);
		if (!assetsFolder.exists()) {
			throw new SiteWhereException("Assets subfolder not found. Looking for: "
					+ assetsFolder.getAbsolutePath());
		}
		File hardwareConfig = new File(assetsFolder, HARDWARE_CONFIG_FILENAME);
		if (!hardwareConfig.exists()) {
			throw new SiteWhereException("Hardware assets file missing. Looking for: "
					+ hardwareConfig.getAbsolutePath());
		}
		LOGGER.info("Loading hardware assets from: " + hardwareConfig.getAbsolutePath());

		// Unmarshal assets from XML file and store in data object.
		List<HardwareAsset> assets = new ArrayList<HardwareAsset>();
		this.deviceAssetsById = new HashMap<String, HardwareAsset>();
		this.hardwareAssetsById = new HashMap<String, HardwareAsset>();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(FileSystemHardwareAssets.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			FileSystemHardwareAssets xmlAssets = (FileSystemHardwareAssets) jaxbUnmarshaller
					.unmarshal(hardwareConfig);
			for (FileSystemHardwareAsset xmlAsset : xmlAssets.getHardwareAssets()) {
				HardwareAsset asset = new HardwareAsset();
				asset.setId(xmlAsset.getId());
				asset.setName(xmlAsset.getName());
				asset.setDescription(xmlAsset.getDescription());
				asset.setSku(xmlAsset.getSku());
				asset.setImageUrl(xmlAsset.getImageUrl());
				for (FileSystemAssetProperty xmlProperty : xmlAsset.getProperties()) {
					asset.setProperty(xmlProperty.getName(), xmlProperty.getValue());
				}
				assets.add(asset);
				if (xmlAsset.isDevice()) {
					deviceAssetsById.put(asset.getId(), asset);
				} else {
					hardwareAssetsById.put(asset.getId(), asset);
				}
			}
			showLoadResults();
		} catch (Exception e) {
			throw new SiteWhereException("Unable to unmarshal hardware assets file.", e);
		}
	}

	/**
	 * Log the number of assets loaded for each type.
	 */
	protected void showLoadResults() {
		String message = "Loaded " + deviceAssetsById.size() + " device assets.";
		LOGGER.info(message);
		message = "Loaded " + hardwareAssetsById.size() + " hardware assets.";
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
		if ((type == AssetType.Hardware) || (type == AssetType.Device)) {
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
	public HardwareAsset getAssetById(AssetType type, String id) throws SiteWhereException {
		if (type == AssetType.Device) {
			return deviceAssetsById.get(id);
		}
		return hardwareAssetsById.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#search(com.sitewhere.spi.asset.AssetType,
	 * java.lang.String)
	 */
	public List<HardwareAsset> search(AssetType type, String criteria) throws SiteWhereException {
		criteria = criteria.toLowerCase();
		List<HardwareAsset> results = new ArrayList<HardwareAsset>();
		Map<String, HardwareAsset> cache = (type == AssetType.Device) ? deviceAssetsById : hardwareAssetsById;
		if (criteria.length() == 0) {
			results.addAll(cache.values());
		} else {
			for (HardwareAsset asset : cache.values()) {
				if (matcher.isHardwareMatch(asset, criteria)) {
					results.add(asset);
				}
			}
		}
		Collections.sort(results);
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