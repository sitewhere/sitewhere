/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.configuration;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.resource.IResource;

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
     * Indicates if a staged configuration exists for the tenant.
     * 
     * @return
     */
    public boolean hasStagedConfiguration();

    /**
     * Get tenant resource that corresponds to path.
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
     * Gets the active configuration resource for a given tenant.
     * 
     * @return
     * @throws SiteWhereException
     */
    public IResource getActiveTenantConfiguration() throws SiteWhereException;

    /**
     * Create a default configuration resource for a new tenant.
     * 
     * @return
     * @throws SiteWhereException
     */
    public IResource createDefaultTenantConfiguration() throws SiteWhereException;

    /**
     * Gets the staged configuration resource for a given tenant. Returns null
     * if no configuration is staged.
     * 
     * @return
     * @throws SiteWhereException
     */
    public IResource getStagedTenantConfiguration() throws SiteWhereException;

    /**
     * Stage a new tenant configuration. This stores the new configuration
     * separately from the active configuration. The staged configuration will
     * be made active the next time the tenant is restarted.
     * 
     * @param content
     * @return
     * @throws SiteWhereException
     */
    public IResource stageTenantConfiguration(byte[] content) throws SiteWhereException;

    /**
     * Transition the staged tenant configuration to the active tenant
     * configuration, backing up the active configuration in the process.
     * 
     * @throws SiteWhereException
     */
    public void transitionStagedToActiveTenantConfiguration() throws SiteWhereException;
}