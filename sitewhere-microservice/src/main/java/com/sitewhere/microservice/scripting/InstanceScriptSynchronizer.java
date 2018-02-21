/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.microservice.scripting.IScriptSynchronizer;

/**
 * Implementation of {@link IScriptSynchronizer} that copies instance-level
 * scripts from Zookeeper to the local filesystem of a microservice.
 * 
 * @author Derek
 */
public class InstanceScriptSynchronizer extends ScriptSynchronizer {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(InstanceScriptSynchronizer.class);

    /** File system root */
    private File fileSystemRoot;

    /** Zookeeper root path */
    private String zkScriptRootPath;

    public InstanceScriptSynchronizer(IConfigurableMicroservice microservice) {
	super(microservice);
	setFileSystemRoot(new File(getMicroservice().getInstanceSettings().getFileSystemStorageRoot()));
	setZkScriptRootPath(getMicroservice().getInstanceZkPath());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.groovy.IScriptSynchronizer#
     * getFileSystemRoot()
     */
    @Override
    public File getFileSystemRoot() {
	return fileSystemRoot;
    }

    public void setFileSystemRoot(File fileSystemRoot) {
	this.fileSystemRoot = fileSystemRoot;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.groovy.IScriptSynchronizer#
     * getZkScriptRootPath()
     */
    @Override
    public String getZkScriptRootPath() {
	return zkScriptRootPath;
    }

    public void setZkScriptRootPath(String zkScriptRootPath) {
	this.zkScriptRootPath = zkScriptRootPath;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }
}