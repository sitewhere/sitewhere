/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.user;

import java.util.List;

import com.sitewhere.spi.common.IMetadataProviderEntity;
import com.sitewhere.spi.server.ISiteWhereTenantEngineState;

/**
 * Interface for information about a tenant.
 * 
 * @author Derek
 */
public interface ITenant extends IMetadataProviderEntity {

	/**
	 * Get unique tenant id.
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * Get tenant name.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Get token that devices pass to identify tenant.
	 * 
	 * @return
	 */
	public String getAuthenticationToken();

	/**
	 * Get URL for tenant logo.
	 * 
	 * @return
	 */
	public String getLogoUrl();

	/**
	 * Get list of users authorized to access the tenant.
	 * 
	 * @return
	 */
	public List<String> getAuthorizedUserIds();

	/**
	 * Get engine configuration details.
	 * 
	 * @return
	 */
	public String getEngineConfiguration();

	/**
	 * Get runtime state of tenant engine.
	 * 
	 * @return
	 */
	public ISiteWhereTenantEngineState getEngineState();
}