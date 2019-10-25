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
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Management interface for interacting with scripts.
 */
public interface IScriptManagement extends ILifecycleComponent {

    /**
     * Get list of metadata entries for all scripts in the given microservice.
     * 
     * @param identifier
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    List<IScriptMetadata> getScriptMetadataList(IFunctionIdentifier identifier, UUID tenantId)
	    throws SiteWhereException;

    /**
     * Get metadata for a given script.
     * 
     * @param identifier
     * @param tenantId
     * @param scriptId
     * @return
     * @throws SiteWhereException
     */
    IScriptMetadata getScriptMetadata(IFunctionIdentifier identifier, UUID tenantId, String scriptId)
	    throws SiteWhereException;

    /**
     * Creates a new script.
     * 
     * @param identifier
     * @param tenantId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IScriptMetadata createScript(IFunctionIdentifier identifier, UUID tenantId, IScriptCreateRequest request)
	    throws SiteWhereException;

    /**
     * Get content for script based on unique script id and version identifier.
     * 
     * @param identifier
     * @param tenantId
     * @param scriptId
     * @param versionId
     * @return
     * @throws SiteWhereException
     */
    byte[] getScriptContent(IFunctionIdentifier identifier, UUID tenantId, String scriptId, String versionId)
	    throws SiteWhereException;

    /**
     * Update an existing script.
     * 
     * @param identifier
     * @param tenantId
     * @param scriptId
     * @param versionId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    IScriptMetadata updateScript(IFunctionIdentifier identifier, UUID tenantId, String scriptId, String versionId,
	    IScriptCreateRequest request) throws SiteWhereException;

    /**
     * Creates a new version of a script that is a clone of the given version.
     * 
     * @param identifier
     * @param tenantId
     * @param scriptId
     * @param versionId
     * @param comment
     * @return
     * @throws SiteWhereException
     */
    IScriptVersion cloneScript(IFunctionIdentifier identifier, UUID tenantId, String scriptId, String versionId,
	    String comment) throws SiteWhereException;

    /**
     * Activate the given version of the script. This sets the active id and forces
     * the content to be copied into the scripts content folder whether it has been
     * updated or not.
     * 
     * @param identifier
     * @param tenantId
     * @param scriptId
     * @param versionId
     * @return
     * @throws SiteWhereException
     */
    IScriptMetadata activateScript(IFunctionIdentifier identifier, UUID tenantId, String scriptId, String versionId)
	    throws SiteWhereException;

    /**
     * Delete an existing script including metadata and all versions.
     * 
     * @param identifier
     * @param tenantId
     * @param scriptId
     * @return
     * @throws SiteWhereException
     */
    IScriptMetadata deleteScript(IFunctionIdentifier identifier, UUID tenantId, String scriptId)
	    throws SiteWhereException;

    /**
     * Get path for Zk container that holds script metadata.
     * 
     * @param identifier
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    String getScriptMetadataZkPath(IFunctionIdentifier identifier, UUID tenantId) throws SiteWhereException;

    /**
     * Get path for Zk container that holds script content.
     * 
     * @param identifier
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    String getScriptContentZkPath(IFunctionIdentifier identifier, UUID tenantId) throws SiteWhereException;
}