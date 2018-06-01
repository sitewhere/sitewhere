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

import com.sitewhere.microservice.zookeeper.ZkUtils;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.microservice.scripting.IScriptSynchronizer;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Base class for script synchronizers.
 * 
 * @author Derek
 */
public abstract class ScriptSynchronizer extends LifecycleComponent implements IScriptSynchronizer {

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);
	((IConfigurableMicroservice<?>) getMicroservice()).getConfigurationMonitor().getListeners().add(this);
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
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#terminate(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void terminate(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.terminate(monitor);
	((IConfigurableMicroservice<?>) getMicroservice()).getConfigurationMonitor().getListeners().remove(this);
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
		getLogger().debug("Deleted script at path '" + existing.getAbsolutePath() + "'.");
	    } catch (IOException e) {
		throw new SiteWhereException("Unable to delete script from filesystem.", e);
	    }
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationListener#
     * onConfigurationCacheInitialized()
     */
    @Override
    public void onConfigurationCacheInitialized() {
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationListener#
     * onConfigurationAdded(java.lang.String, byte[])
     */
    @Override
    public void onConfigurationAdded(String path, byte[] data) {
	try {
	    if (isScriptContent(path)) {
		add(getRelativePath(path));
	    }
	} catch (SiteWhereException e) {
	    getLogger().error("Error processing added script.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationListener#
     * onConfigurationUpdated(java.lang.String, byte[])
     */
    @Override
    public void onConfigurationUpdated(String path, byte[] data) {
	try {
	    if (isScriptContent(path)) {
		update(getRelativePath(path));
	    }
	} catch (SiteWhereException e) {
	    getLogger().error("Error processing updated script.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationListener#
     * onConfigurationDeleted(java.lang.String)
     */
    @Override
    public void onConfigurationDeleted(String path) {
	try {
	    if (isScriptContent(path)) {
		delete(getRelativePath(path));
	    }
	} catch (SiteWhereException e) {
	    getLogger().error("Error processing deleted script.", e);
	}
    }

    /**
     * Checks whether path is script content.
     * 
     * @param path
     * @return
     * @throws SiteWhereException
     */
    protected boolean isScriptContent(String path) throws SiteWhereException {
	if (path.startsWith(getZkScriptRootPath())) {
	    if (path.substring(getZkScriptRootPath().length()).length() == 0) {
		return false;
	    }
	    return true;
	}
	return false;
    }

    /**
     * Get path relative to content root.
     * 
     * @param path
     * @return
     * @throws SiteWhereException
     */
    protected String getRelativePath(String path) throws SiteWhereException {
	return path.substring(getZkScriptRootPath().length() + 1);
    }

    /**
     * Copy Zookeeper content to filesystem.
     * 
     * @param zkPath
     * @throws SiteWhereException
     */
    protected void copy(String zkPath) throws SiteWhereException {
	byte[] content = getZkContent(zkPath);
	if (isFolder(zkPath)) {
	    // Only copy files.
	    return;
	}
	FileOutputStream output = null;
	File out = getFileFor(zkPath);
	try {
	    if (!out.getParentFile().exists()) {
		getLogger().warn("Script parent folder '" + out.getParentFile().getAbsolutePath()
			+ "' does not exist. Creating.");
		if (!out.getParentFile().mkdirs()) {
		    getLogger().warn("Mkdirs for '" + out.getParentFile().getAbsolutePath() + "' failed.");
		}
	    }
	    output = new FileOutputStream(out);
	    ByteArrayInputStream input = new ByteArrayInputStream(content);
	    IOUtils.copy(input, output);
	    getLogger().debug("Copied script content (" + content.length + " bytes) from '" + zkPath + "' to '"
		    + out.getAbsolutePath() + "'.");
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to copy script from Zookeeper path '" + zkPath + "' to file '"
		    + out.getAbsolutePath() + "'.", e);
	} finally {
	    IOUtils.closeQuietly(output);
	}
    }

    /**
     * Determine whether path represents a folder.
     * 
     * @param pathString
     * @return
     */
    protected boolean isFolder(String pathString) {
	Path path = Paths.get(pathString);
	path = path.getFileName();
	if ((path == null) || (path.toString().indexOf('.') == -1)) {
	    return true;
	}
	return false;
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
}