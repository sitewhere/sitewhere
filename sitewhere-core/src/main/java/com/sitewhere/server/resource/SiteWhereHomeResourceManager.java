/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.resource;

import java.io.File;

import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Extends {@link FileSystemResourceManager} to set root of resource tree based
 * on the SiteWhere home ('sitewhere.home') environment variable.
 * 
 * @author Derek
 */
public class SiteWhereHomeResourceManager extends FileSystemResourceManager {

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.resource.FileSystemResourceManager#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	setRootFolder(calculateConfigurationPath());
	super.start(monitor);
    }

    /**
     * Calculate the system configuration path relative to the SiteWhere home
     * environment variable.
     * 
     * @return
     * @throws SiteWhereException
     */
    public static File calculateConfigurationPath() throws SiteWhereException {
	File swFolder = SiteWhereServer.getSiteWhereHomeFolder();
	File confDir = new File(swFolder, "conf");
	if (!confDir.exists()) {
	    throw new SiteWhereException(
		    "'SiteWhere configuration folder does not exist. Looking in: " + confDir.getAbsolutePath());
	}
	return confDir;
    }
}