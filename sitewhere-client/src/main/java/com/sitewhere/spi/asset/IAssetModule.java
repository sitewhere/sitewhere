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

/**
 * Interface for a module that provides access to one or more asset types.
 * 
 * @author dadams
 */
public interface IAssetModule<T extends IAsset> {

	/**
	 * Start the module.
	 * 
	 * @throws SiteWhereException
	 */
	public void start() throws SiteWhereException;

	/**
	 * Stop the module.
	 * 
	 * @throws SiteWhereException
	 */
	public void stop() throws SiteWhereException;

	/**
	 * Get the unique module identifier.
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * Get the module name.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Indicates if the module can be used to locate assets of a given type.
	 * 
	 * @param type
	 * @return
	 */
	public boolean isAssetTypeSupported(AssetType type);

	/**
	 * Get an asset by unique id.
	 * 
	 * @param type
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	public T getAssetById(AssetType type, String id) throws SiteWhereException;

	/**
	 * Search for all assets of a given type that meet the criteria.
	 * 
	 * @param type
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public List<T> search(AssetType type, String criteria) throws SiteWhereException;

	/**
	 * Refresh any cached data in the module.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public ICommandResponse refresh() throws SiteWhereException;
}