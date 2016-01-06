/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.asset;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.configuration.ITenantConfigurationResolver;
import com.sitewhere.spi.server.IModelInitializer;

/**
 * Class that initializes the asset model with data needed to bootstrap the system.
 * 
 * @author Derek
 */
public interface IAssetModelInitializer extends IModelInitializer {

	/**
	 * Initialize the asset model.
	 * 
	 * @param configuration
	 * @param assetManagement
	 * @throws SiteWhereException
	 */
	public void initialize(ITenantConfigurationResolver configuration, IAssetManagement assetManagement)
			throws SiteWhereException;
}