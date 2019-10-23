/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.scripting;

import java.io.File;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.configuration.IInstanceConfigurationListener;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Common interface for synchronizing scripts from Zookeeper to the local
 * filesystem for a microservice.
 * 
 * @author Derek
 */
public interface IScriptSynchronizer extends ILifecycleComponent, IInstanceConfigurationListener {

    /**
     * Get root folder on filesystem where scripts will be stored.
     * 
     * @return
     * @throws SiteWhereException
     */
    public File getFileSystemRoot() throws SiteWhereException;

    /**
     * Get root path for matching Zookeeper updates.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getZkScriptRootPath() throws SiteWhereException;

    /**
     * Add script from the given relative path.
     * 
     * @param zkPath
     * @throws SiteWhereException
     */
    public void add(String relativePath) throws SiteWhereException;

    /**
     * Update script from the given relative path.
     * 
     * @param zkPath
     * @throws SiteWhereException
     */
    public void update(String relativePath) throws SiteWhereException;

    /**
     * Delete script corresponding to the given relative path.
     * 
     * @param zkPath
     * @throws SiteWhereException
     */
    public void delete(String relativePath) throws SiteWhereException;
}