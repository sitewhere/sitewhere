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
import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.resource.IResourceManager;
import com.sitewhere.spi.system.IVersion;

/**
 * Pluggable resolver for interacting with global configuration data.
 * 
 * @author Derek
 */
public interface IGlobalConfigurationResolver {

	/**
	 * Gets the URI for the root folder on the filesystem where configuration
	 * elements can be stored. This is being phased out by use of the
	 * {@link IResourceManager} implementation and will be removed.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	@Deprecated
	public URI getFilesystemConfigurationRoot() throws SiteWhereException;

	/**
	 * Gets a resource for the given global path.
	 * 
	 * @param path
	 * @return
	 * @throws SiteWhereException
	 */
	public IResource getResourceForPath(String path) throws SiteWhereException;

	/**
	 * Get an asset resource based on relative path.
	 * 
	 * @param path
	 * @return
	 * @throws SiteWhereException
	 */
	public IResource getAssetResource(String path) throws SiteWhereException;

	/**
	 * Get a script resource based on relative path.
	 * 
	 * @param path
	 * @return
	 * @throws SiteWhereException
	 */
	public IResource getScriptResource(String path) throws SiteWhereException;

	/**
	 * Get the global configuration resource.
	 * 
	 * @param version
	 * @return
	 * @throws SiteWhereException
	 */
	public IResource getGlobalConfiguration(IVersion version) throws SiteWhereException;

	/**
	 * Get server state information as a resource.
	 * 
	 * @param version
	 * @return
	 * @throws SiteWhereException
	 */
	public IResource resolveServerState(IVersion version) throws SiteWhereException;

	/**
	 * Store information about server state.
	 * 
	 * @param version
	 * @param data
	 * @throws SiteWhereException
	 */
	public void storeServerState(IVersion version, byte[] data) throws SiteWhereException;
}