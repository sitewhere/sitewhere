/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.asset;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Interface for interacting with the asset module manager.
 * 
 * @author dadams
 */
public interface IAssetModuleManager extends ILifecycleComponent {

	/**
	 * Get the list of asset modules.
	 * 
	 * @return
	 */
	public List<IAssetModule<?>> listModules();

	/**
	 * Calls the refresh method on each asset module and returns a list of responses.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public List<ICommandResponse> refreshModules() throws SiteWhereException;

	/**
	 * Finds an asset in a given module.
	 * 
	 * @param assetModuleId
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	public IAsset getAssetById(String assetModuleId, String id) throws SiteWhereException;

	/**
	 * Search an asset module for assets matching the given criteria.
	 * 
	 * @param assetModuleId
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public List<? extends IAsset> search(String assetModuleId, String criteria) throws SiteWhereException;
}