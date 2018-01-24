/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.microservice.zookeeper.ZkUtils;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.microservice.groovy.IScriptSynchronizer;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Base class for script synchronizers.
 * 
 * @author Derek
 */
public abstract class ScriptSynchronizer extends LifecycleComponent implements IScriptSynchronizer {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Microservice reference */
    private IConfigurableMicroservice microservice;

    public ScriptSynchronizer(IConfigurableMicroservice microservice) {
	this.microservice = microservice;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	// Copy all scipts from Zk to local.
	ZkUtils.copyFolderRecursivelyFromZk(getMicroservice().getZookeeperManager().getCurator(), getZkScriptRootPath(),
		getFileSystemRoot(), getZkScriptRootPath());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.groovy.IScriptSynchronizer#add(java.lang.
     * String)
     */
    @Override
    public void add(String relativePath) throws SiteWhereException {
	copy(getZkScriptRootPath() + "/" + relativePath);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.groovy.IScriptSynchronizer#update(java.
     * lang.String)
     */
    @Override
    public void update(String relativePath) throws SiteWhereException {
	copy(getZkScriptRootPath() + "/" + relativePath);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.groovy.IScriptSynchronizer#delete(java.
     * lang.String)
     */
    @Override
    public void delete(String relativePath) throws SiteWhereException {
	File existing = getFileFor(getZkScriptRootPath() + "/" + relativePath);
	if (existing.exists()) {
	    try {
		Files.delete(existing.toPath());
		LOGGER.info("Deleted script at path '" + existing.getAbsolutePath() + "'.");
	    } catch (IOException e) {
		throw new SiteWhereException("Unable to delete script from filesystem.", e);
	    }
	}
    }

    /**
     * Copy Zookeeper content to filesystem.
     * 
     * @param zkPath
     * @throws SiteWhereException
     */
    protected void copy(String zkPath) throws SiteWhereException {
	byte[] content = getZkContent(zkPath);
	FileOutputStream output = null;
	try {
	    File out = getFileFor(zkPath);
	    if (!out.getParentFile().exists()) {
		out.getParentFile().mkdirs();
	    }
	    output = new FileOutputStream(out);
	    ByteArrayInputStream input = new ByteArrayInputStream(content);
	    IOUtils.copy(input, output);
	    LOGGER.info("Copied script from '" + zkPath + "' to '" + out.getAbsolutePath() + "'.");
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to copy script from Zookeeper to filesystem.", e);
	} finally {
	    IOUtils.closeQuietly(output);
	}
    }

    /**
     * Get Zookeeper content from the given path.
     * 
     * @param zkPath
     * @return
     * @throws SiteWhereException
     */
    protected byte[] getZkContent(String zkPath) throws SiteWhereException {
	try {
	    return getMicroservice().getZookeeperManager().getCurator().getData().forPath(zkPath);
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to get Zookeeper content for path '" + zkPath + "'.");
	}
    }

    /**
     * Get the file (relative to filesystem root) that corresponds to the Zookeeper
     * path.
     * 
     * @param zkPath
     * @return
     * @throws SiteWhereException
     */
    protected File getFileFor(String zkPath) throws SiteWhereException {
	try {
	    Path root = Paths.get(getZkScriptRootPath());
	    Path relative = root.relativize(Paths.get(zkPath));
	    Path fsRoot = getFileSystemRoot().toPath();
	    return fsRoot.resolve(relative).toFile();
	} catch (Throwable e) {
	    throw new SiteWhereException("Unable to get Zookeeper script content.", e);
	}
    }

    protected IConfigurableMicroservice getMicroservice() {
	return microservice;
    }

    protected void setMicroservice(IConfigurableMicroservice microservice) {
	this.microservice = microservice;
    }
}