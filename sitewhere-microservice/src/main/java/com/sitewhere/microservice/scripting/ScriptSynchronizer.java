/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import java.io.File;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.scripting.IScriptContext;
import com.sitewhere.spi.microservice.scripting.IScriptSynchronizer;
import com.sitewhere.spi.microservice.scripting.ScriptType;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Handles process of copying script artifacts onto the local container
 * filesystem so that the Groovy runtime can properly detect
 */
public class ScriptSynchronizer extends LifecycleComponent implements IScriptSynchronizer {

    /** File system root where scripts are copied */
    private File fileSystemRoot;

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);
	this.fileSystemRoot = new File(getMicroservice().getInstanceSettings().getFileSystemStorageRoot());
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptSynchronizer#add(com.
     * sitewhere.spi.microservice.scripting.IScriptContext,
     * com.sitewhere.spi.microservice.scripting.ScriptType, java.lang.String,
     * byte[])
     */
    @Override
    public String add(IScriptContext context, ScriptType type, String name, byte[] content) throws SiteWhereException {
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptSynchronizer#resolve(com.
     * sitewhere.spi.microservice.scripting.IScriptContext,
     * com.sitewhere.spi.microservice.scripting.ScriptType, java.lang.String)
     */
    @Override
    public String resolve(IScriptContext context, ScriptType type, String name) throws SiteWhereException {
	File relative = new File(getFileSystemRoot() + File.separator + context.getBasePath());
	File resolved = new File(relative, name);
	return resolved.getAbsolutePath();
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptSynchronizer#
     * getFileSystemRoot()
     */
    @Override
    public File getFileSystemRoot() {
	return fileSystemRoot;
    }

    public void setFileSystemRoot(File fileSystemRoot) {
	this.fileSystemRoot = fileSystemRoot;
    }
}