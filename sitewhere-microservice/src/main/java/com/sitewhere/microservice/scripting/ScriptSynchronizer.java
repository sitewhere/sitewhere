/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

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
	getLogger().debug(String.format("File system root for script synchronizer is %s.",
		getMicroservice().getInstanceSettings().getFileSystemStorageRoot()));
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
    public Path add(IScriptContext context, ScriptType type, String name, byte[] content) throws SiteWhereException {
	Path relative = resolve(context, type, name);
	Path destination = getFileSystemRoot().toPath().resolve(relative);
	getLogger().debug(String.format("Resolving script file -> Root: [%s] Relative [%s]",
		getFileSystemRoot().toPath().toString(), relative.toString()));

	try {
	    destination.getParent().toFile().mkdirs();
	    IOUtils.write(content, new FileWriter(destination.toFile()), Charset.defaultCharset());
	    getLogger().debug(String.format("Synchronized file '%s' to disk. Exists: %s",
		    destination.toFile().getAbsolutePath(), String.valueOf(destination.toFile().exists())));
	    URLConnection conn = destination.toFile().toURI().toURL().openConnection();
	    conn.connect();
	    InputStream stream = conn.getInputStream();
	    stream.close();
	} catch (IOException e) {
	    throw new SiteWhereException(String.format("Unable to copy script to disk at path '%s'.", destination), e);
	}

	return relative;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptSynchronizer#resolve(com.
     * sitewhere.spi.microservice.scripting.IScriptContext,
     * com.sitewhere.spi.microservice.scripting.ScriptType, java.lang.String)
     */
    @Override
    public Path resolve(IScriptContext context, ScriptType type, String name) throws SiteWhereException {
	return Paths.get(context.getBasePath(), getPathForType(type), name);
    }

    /**
     * Get path based on script type.
     * 
     * @param type
     * @return
     * @throws SiteWhereException
     */
    protected String getPathForType(ScriptType type) throws SiteWhereException {
	switch (type) {
	case Initializer: {
	    return "initializer";
	}
	case ManagedScript: {
	    return "scripts";
	}
	}
	throw new SiteWhereException(String.format("Unknown script type: %s", type.name()));
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