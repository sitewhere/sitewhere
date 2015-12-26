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
	 * Get the base configuration URL.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public URI getConfigurationRoot() throws SiteWhereException;

	/**
	 * Get the global configuration.
	 * 
	 * @param version
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] getGlobalConfiguration(IVersion version) throws SiteWhereException;

	/**
	 * Gets the active configuration for a given tenant.
	 * 
	 * @param tenant
	 * @param version
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] getActiveTenantConfiguration(ITenant tenant, IVersion version) throws SiteWhereException;

	/**
	 * Create a default configuration for a new tenant.
	 * 
	 * @param tenant
	 * @param version
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] createDefaultTenantConfiguration(ITenant tenant, IVersion version)
			throws SiteWhereException;

	/**
	 * Gets the staged configuration for a given tenant. Returns null if no configuration
	 * is staged.
	 * 
	 * @param tenant
	 * @param version
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] getStagedTenantConfiguration(ITenant tenant, IVersion version) throws SiteWhereException;

	/**
	 * Stage a new tenant configuration. This stores the new configuration separately from
	 * the active configuration. The staged configuration will be made active the next
	 * time the tenant is restarted.
	 * 
	 * @param configuration
	 * @param tenant
	 * @param version
	 * @throws SiteWhereException
	 */
	public void stageTenantConfiguration(byte[] configuration, ITenant tenant, IVersion version)
			throws SiteWhereException;

	/**
	 * Transition the staged tenant configuration to the active tenant configuration,
	 * backing up the active configuration in the process.
	 * 
	 * @param tenant
	 * @param version
	 * @throws SiteWhereException
	 */
	public void transitionStagedToActiveTenantConfiguration(ITenant tenant, IVersion version)
			throws SiteWhereException;

	/**
	 * Get binary form of server state information.
	 * 
	 * @param version
	 * @return
	 * @throws SiteWhereException
	 */
	public byte[] resolveServerState(IVersion version) throws SiteWhereException;

	/**
	 * Store information about server state.
	 * 
	 * @param version
	 * @param data
	 * @throws SiteWhereException
	 */
	public void storeServerState(IVersion version, byte[] data) throws SiteWhereException;
}