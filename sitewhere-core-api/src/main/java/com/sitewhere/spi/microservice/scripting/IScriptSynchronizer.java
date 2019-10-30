/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.scripting;

import java.io.File;
import java.nio.file.Path;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Common interface for synchronizing script files to the local filesystem on a
 * container so that the Groovy script engine can properly detect changes.
 */
public interface IScriptSynchronizer extends ILifecycleComponent {

    /**
     * Get root folder on filesystem where scripts will be stored.
     * 
     * @return
     * @throws SiteWhereException
     */
    public File getFileSystemRoot() throws SiteWhereException;

    /**
     * Add a script that should be synchronized to disk.
     * 
     * @param context
     * @param type
     * @param name
     * @param content
     * @return
     * @throws SiteWhereException
     */
    public Path add(IScriptContext context, ScriptType type, String name, byte[] content) throws SiteWhereException;

    /**
     * Produce path to script that has been serialized to disk.
     * 
     * @param context
     * @param type
     * @param name
     * @return
     * @throws SiteWhereException
     */
    public Path resolve(IScriptContext context, ScriptType type, String name) throws SiteWhereException;
}