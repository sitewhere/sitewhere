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

/**
 * Pluggable resolver for interacting with global configuration data.
 * 
 * @author Derek
 */
public interface IGlobalConfigurationResolver {

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