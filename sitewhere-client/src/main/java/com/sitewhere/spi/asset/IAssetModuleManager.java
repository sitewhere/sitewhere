/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.spi.asset;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.device.DeviceAssignmentType;

/**
 * Interface for interacting with the asset module manager.
 * 
 * @author dadams
 */
public interface IAssetModuleManager {

	/**
	 * Start the asset module manager.
	 * 
	 * @throws SiteWhereException
	 */
	public void start() throws SiteWhereException;

	/**
	 * Stop the asset module manager.
	 */
	public void stop();

	/**
	 * Get the list of asset modules.
	 * 
	 * @return
	 */
	public List<IAssetModule<?>> getModules();

	/**
	 * Calls the refresh method on each asset module and returns a list of responses.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public List<ICommandResponse> refreshModules() throws SiteWhereException;

	/**
	 * Finds an asset of the given type by id by querying the modules.
	 * 
	 * @param type
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	public IAsset getAssetById(AssetType type, String id) throws SiteWhereException;

	/**
	 * Finds an associated asset based on unique id.
	 * 
	 * @param type
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	public IAsset getAssignedAsset(DeviceAssignmentType type, String id) throws SiteWhereException;

	/**
	 * Search for an asset of the given type based on the given criteria.
	 * 
	 * @param type
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public List<? extends IAsset> search(AssetType type, String criteria) throws SiteWhereException;
}