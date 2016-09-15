/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.rest.model.resource.request.ResourceCreateRequest;
import com.sitewhere.server.resource.SiteWhereHomeResourceManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.configuration.IGlobalConfigurationResolver;
import com.sitewhere.spi.resource.IMultiResourceCreateResponse;
import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.resource.IResourceCreateError;
import com.sitewhere.spi.resource.IResourceManager;
import com.sitewhere.spi.resource.ResourceCreateMode;
import com.sitewhere.spi.resource.ResourceType;
import com.sitewhere.spi.resource.request.IResourceCreateRequest;
import com.sitewhere.spi.system.IVersion;

/**
 * Resolves global configuration settings within a Tomcat instance.
 * 
 * @author Derek
 */
public class ResourceManagerGlobalConfigurationResolver implements IGlobalConfigurationResolver {

    /** Static logger instance */
    public static Logger LOGGER = LogManager.getLogger();

    /** File name for SiteWhere global configuration file */
    public static final String GLOBAL_CONFIG_FILE_NAME = "sitewhere-server.xml";

    /** File name for SiteWhere state information in JSON format */
    public static final String STATE_FILE_NAME = "sitewhere-state.json";

    /** Folder containing tenant asset resources */
    public static final String ASSETS_FOLDER = "assets";

    /** Folder containing tenant script resources */
    public static final String SCRIPTS_FOLDER = "scripts";

    /** Resource manager implementation */
    private IResourceManager resourceManager;

    /** Deprecated link to file system configuration root */
    private URI configurationRoot;

    public ResourceManagerGlobalConfigurationResolver(IResourceManager resourceManager) {
	this.resourceManager = resourceManager;
	try {
	    configurationRoot = SiteWhereHomeResourceManager.calculateConfigurationPath().toURI();
	} catch (SiteWhereException e) {
	    LOGGER.error("Error locating system configuration root.", e);
	    configurationRoot = null;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.configuration.IGlobalConfigurationResolver#
     * getFilesystemConfigurationRoot()
     */
    @Override
    public URI getFilesystemConfigurationRoot() throws SiteWhereException {
	if (configurationRoot != null) {
	    return configurationRoot;
	}
	throw new SiteWhereException("Configuration root not set.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.configuration.IGlobalConfigurationResolver#
     * getResourceForPath(java.lang.String)
     */
    @Override
    public IResource getResourceForPath(String path) throws SiteWhereException {
	return getResourceManager().getGlobalResource(path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.configuration.IGlobalConfigurationResolver#
     * getAssetResource(java.lang.String)
     */
    @Override
    public IResource getAssetResource(String path) throws SiteWhereException {
	return getResourceManager().getGlobalResource(ASSETS_FOLDER + File.separator + path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.configuration.IGlobalConfigurationResolver#
     * getScriptResource(java.lang.String)
     */
    @Override
    public IResource getScriptResource(String path) throws SiteWhereException {
	return getResourceManager().getGlobalResource(SCRIPTS_FOLDER + File.separator + path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.configuration.IGlobalConfigurationResolver#
     * getGlobalConfiguration(com.sitewhere.spi.system.IVersion)
     */
    @Override
    public IResource getGlobalConfiguration(IVersion version) throws SiteWhereException {
	return getResourceManager().getGlobalResource(GLOBAL_CONFIG_FILE_NAME);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.configuration.IGlobalConfigurationResolver#
     * resolveServerState(com.sitewhere.spi.system.IVersion)
     */
    @Override
    public IResource resolveServerState(IVersion version) throws SiteWhereException {
	return getResourceManager().getGlobalResource(STATE_FILE_NAME);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.configuration.IGlobalConfigurationResolver#
     * storeServerState(com.sitewhere.spi.system.IVersion, byte[])
     */
    @Override
    public void storeServerState(IVersion version, byte[] data) throws SiteWhereException {
	List<IResourceCreateRequest> requests = new ArrayList<IResourceCreateRequest>();
	ResourceCreateRequest request = new ResourceCreateRequest();
	request.setPath(STATE_FILE_NAME);
	request.setResourceType(ResourceType.ConfigurationFile);
	request.setContent(data);
	requests.add(request);
	IMultiResourceCreateResponse response = getResourceManager().createGlobalResources(requests,
		ResourceCreateMode.PUSH_NEW_VERSION);
	if (response.getErrors().size() > 0) {
	    IResourceCreateError error = response.getErrors().get(0);
	    throw new SiteWhereException("Unable to save server state: " + error.getReason().name());
	}
    }

    public IResourceManager getResourceManager() {
	return resourceManager;
    }

    public void setResourceManager(IResourceManager resourceManager) {
	this.resourceManager = resourceManager;
    }
}