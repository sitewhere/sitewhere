/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.configuration;

import java.net.URI;

import com.sitewhere.spi.SiteWhereException;

/**
 * Pluggable resolver for interacting with global configuration data.
 * 
 * @author Derek
 */
public interface ITenantConfigurationResolver {

	/**
	 * Get the global configuration resolver.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public IGlobalConfigurationResolver getGlobalConfigurationResolver() throws SiteWhereException;

	/**
	 * Indicates a configuration exists for the tenant.
	 * 
	 * @return
	 */
	public boolean hasValidConfiguration();

	/**
	 * Get URI for locating asset resources.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public URI getAssetResourcesRoot() throws SiteWhereException;

	/**
	 * Get URI for locating script resources.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public URI getScriptResourcesRoot() throws SiteWhereException;

	/**
	 * Gets the active configuration for a given tenant.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] getActiveTenantConfiguration() throws SiteWhereException;

	/**
	 * Create a default configuration for a new tenant.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] createDefaultTenantConfiguration() throws SiteWhereException;

	/**
	 * Gets the staged configuration for a given tenant. Returns null if no configuration
	 * is staged.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] getStagedTenantConfiguration() throws SiteWhereException;

	/**
	 * Stage a new tenant configuration. This stores the new configuration separately from
	 * the active configuration. The staged configuration will be made active the next
	 * time the tenant is restarted.
	 * 
	 * @param configuration
	 * @throws SiteWhereException
	 */
	public void stageTenantConfiguration(byte[] configuration) throws SiteWhereException;

	/**
	 * Transition the staged tenant configuration to the active tenant configuration,
	 * backing up the active configuration in the process.
	 * 
	 * @throws SiteWhereException
	 */
	public void transitionStagedToActiveTenantConfiguration() throws SiteWhereException;
}