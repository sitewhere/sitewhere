/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.server.asset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.device.DeviceAssignmentType;

/**
 * Manages the list of modules
 * 
 * @author dadams
 */
public class AssetModuleManager implements IAssetModuleManager {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(AssetModuleManager.class);

	/** List of asset modules */
	private List<IAssetModule<?>> modules;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModuleManager#start()
	 */
	public void start() throws SiteWhereException {
		for (IAssetModule<?> module : modules) {
			LOGGER.info("Starting asset module: " + module.getName());
			try {
				module.start();
				LOGGER.info("Started asset module: " + module.getName());
			} catch (SiteWhereException e) {
				LOGGER.error("Unable to start asset module: " + module.getName(), e);
			} catch (Throwable t) {
				LOGGER.error("Unhandled exception in asset module: " + module.getName(), t);
				t.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModuleManager#stop()
	 */
	public void stop() {
		for (IAssetModule<?> module : modules) {
			try {
				LOGGER.info("Stopping asset module: " + module.getName());
				module.stop();
				LOGGER.info("Stopped asset module: " + module.getName());
			} catch (SiteWhereException e) {
				LOGGER.error("Unable to stop asset module.", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.asset.IAssetModuleManager#getAssetById(com.sitewhere.spi.asset
	 * .AssetType, java.lang.String)
	 */
	public IAsset getAssetById(AssetType type, String id) throws SiteWhereException {
		for (IAssetModule<?> module : modules) {
			if (module.isAssetTypeSupported(type)) {
				IAsset result = module.getAssetById(type, id);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.asset.IAssetModuleManager#getAssignedAsset(com.sitewhere.spi.
	 * device.DeviceAssignmentType, java.lang.String)
	 */
	public IAsset getAssignedAsset(DeviceAssignmentType type, String id) throws SiteWhereException {
		if (type == DeviceAssignmentType.Person) {
			return getAssetById(AssetType.Person, id);
		} else if (type == DeviceAssignmentType.Hardware) {
			return getAssetById(AssetType.Hardware, id);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.asset.IAssetModuleManager#search(com.sitewhere.spi.asset.AssetType
	 * , java.lang.String)
	 */
	public List<? extends IAsset> search(AssetType type, String criteria) throws SiteWhereException {
		for (IAssetModule<?> module : modules) {
			if (module.isAssetTypeSupported(type)) {
				List<? extends IAsset> results = module.search(type, criteria);
				Collections.sort(results);
				return results;
			}
		}
		return new ArrayList<Asset>();
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