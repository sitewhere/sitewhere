/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.groovy;

import java.io.File;

import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.groovy.IScriptSynchronizer;

/**
 * Implementation of {@link IScriptSynchronizer} that copies instance-level
 * scripts from Zookeeper to the local filesystem of a microservice.
 * 
 * @author Derek
 */
public class InstanceScriptSynchronizer extends ScriptSynchronizer {

    /** File system root */
    private File fileSystemRoot;

    /** Zookeeper root path */
    private String zkScriptRootPath;

    public InstanceScriptSynchronizer(IMicroservice microservice) {
	super(microservice);
	setFileSystemRoot(new File(getMicrosevice().getInstanceSettings().getFileSystemStorageRoot()));
	setZkScriptRootPath(getMicrosevice().getInstanceZkPath());
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
}