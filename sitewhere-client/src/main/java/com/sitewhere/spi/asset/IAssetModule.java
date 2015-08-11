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
 * Interface for a module that provides access to one or more asset types.
 * 
 * @author dadams
 */
public interface IAssetModule<T extends IAsset> extends ILifecycleComponent, IAssetCategory {

	/**
	 * Get an asset by unique id.
	 * 
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	public T getAssetById(String id) throws SiteWhereException;

	/**
	 * Search for all assets of a given type that meet the criteria.
	 * 
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public List<T> search(String criteria) throws SiteWhereException;

	/**
	 * Refresh any cached data in the module.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public ICommandResponse refresh() throws SiteWhereException;
}