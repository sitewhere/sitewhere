/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.scripting;

import java.util.List;
import java.util.UUID;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Management interface for interacting with scripts.
 * 
 * @author Derek
 */
public interface IScriptManagement extends ILifecycleComponent {

    /**
     * Get list of metadata entries for all scripts.
     * 
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    public List<IScriptMetadata> getScriptMetadataList(UUID tenantId) throws SiteWhereException;

    /**
     * Get metadata for a given script.
     * 
     * @param tenantId
     * @param scriptId
     * @return
     * @throws SiteWhereException
     */
    public IScriptMetadata getScriptMetadata(UUID tenantId, String scriptId) throws SiteWhereException;

    /**
     * Creates a new script.
     * 
     * @param tenantId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IScriptMetadata createScript(UUID tenantId, IScriptCreateRequest request) throws SiteWhereException;

    /**
     * Get content for script based on unique script id and version identifier.
     * 
     * @param tenantId
     * @param scriptId
     * @param versionId
     * @return
     * @throws SiteWhereException
     */
    public byte[] getScriptContent(UUID tenantId, String scriptId, String versionId) throws SiteWhereException;

    /**
     * Update an existing script.
     * 
     * @param tenantId
     * @param scriptId
     * @param versionId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IScriptMetadata updateScript(UUID tenantId, String scriptId, String versionId, IScriptCreateRequest request)
	    throws SiteWhereException;

    /**
     * Creates a new version of a script that is a clone of the given version.
     * 
     * @param tenantId
     * @param scriptId
     * @param versionId
     * @param comment
     * @return
     * @throws SiteWhereException
     */
    public IScriptVersion cloneScript(UUID tenantId, String scriptId, String versionId, String comment)
	    throws SiteWhereException;

    /**
     * Activate the given version of the script. This sets the active id and forces
     * the content to be copied into the scripts content folder whether it has been
     * updated or not.
     * 
     * @param tenantId
     * @param scriptId
     * @param versionId
     * @return
     * @throws SiteWhereException
     */
    public IScriptMetadata activateScript(UUID tenantId, String scriptId, String versionId) throws SiteWhereException;

    /**
     * Delete an existing script including metadata and all versions.
     * 
     * @param tenantId
     * @param scriptId
     * @return
     * @throws SiteWhereException
     */
    public IScriptMetadata deleteScript(UUID tenantId, String scriptId) throws SiteWhereException;

    /**
     * Get path for Zk container that holds script metadata.
     * 
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    public String getScriptMetadataZkPath(UUID tenantId) throws SiteWhereException;

    /**
     * Get path for Zk container that holds script content.
     * 
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    public String getScriptContentZkPath(UUID tenantId) throws SiteWhereException;
}