/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.configuration;

import java.io.File;

import org.springframework.context.ApplicationContext;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.system.IVersion;
import com.sitewhere.spi.user.ITenant;

/**
 * Allows for pluggable implementations that can resolve the Spring configuration for the
 * system.
 * 
 * @author Derek
 */
public interface IConfigurationResolver {

	/**
	 * Resolves the SiteWhere Spring configuration context.
	 * 
	 * @param version
	 * @return
	 * @throws SiteWhereException
	 */
	public ApplicationContext resolveSiteWhereContext(IVersion version) throws SiteWhereException;

	/**
	 * Resolve the Spring configuration context for a tenant.
	 * 
	 * @param tenant
	 * @param version
	 * @return
	 * @throws SiteWhereException
	 */
	public ApplicationContext resolveTenantContext(ITenant tenant, IVersion version)
			throws SiteWhereException;

	/**
	 * Gets the root {@link File} where SiteWhere configuration files are stored.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public File getConfigurationRoot() throws SiteWhereException;
}