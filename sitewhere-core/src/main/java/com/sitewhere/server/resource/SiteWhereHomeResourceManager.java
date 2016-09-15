package com.sitewhere.server.resource;

import java.io.File;

import com.sitewhere.spi.SiteWhereException;

/**
 * Extends {@link FileSystemResourceManager} to set root of resource tree based
 * on the SiteWhere home ('sitewhere.home') environment variable.
 * 
 * @author Derek
 */
public class SiteWhereHomeResourceManager extends FileSystemResourceManager {

    /** SiteWhere home directory */
    public static final String SITEWHERE_HOME = "sitewhere.home";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.resource.FileSystemResourceManager#start()
     */
    @Override
    public void start() throws SiteWhereException {
	setRootFolder(calculateConfigurationPath());
	super.start();
    }

    /**
     * Calculate the system configuration path relative to the SiteWhere home
     * environment variable.
     * 
     * @return
     * @throws SiteWhereException
     */
    public static File calculateConfigurationPath() throws SiteWhereException {
	String sitewhere = System.getProperty(SITEWHERE_HOME);
	if (sitewhere == null) {
	    // Support fallback environment variable name.
	    sitewhere = System.getProperty("SITEWHERE_HOME");
	    if (sitewhere == null) {
		throw new SiteWhereException("SiteWhere home environment variable (" + SITEWHERE_HOME + ") not set.");
	    }
	}
	File swFolder = new File(sitewhere);
	if (!swFolder.exists()) {
	    throw new SiteWhereException(
		    "SiteWhere home folder does not exist. Looking in: " + swFolder.getAbsolutePath());
	}
	File confDir = new File(swFolder, "conf");
	if (!confDir.exists()) {
	    throw new SiteWhereException(
		    "'SiteWhere configuration folder does not exist. Looking in: " + confDir.getAbsolutePath());
	}
	return confDir;
    }
}