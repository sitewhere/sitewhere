/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration;

import java.io.File;

import com.sitewhere.spi.SiteWhereException;

/**
 * Configuration resolver that locates files relative to CATALINA_HOME for Tomcat.
 * 
 * @author Derek
 */
public class TomcatGlobalConfigurationResolver extends FileSystemGlobalConfigurationResolver {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.configuration.FileSystemGlobalConfigurationResolver#
	 * getGlobalConfigurationFolder()
	 */
	public File getGlobalConfigurationFolder() throws SiteWhereException {
		String catalina = System.getProperty("catalina.base");
		if (catalina == null) {
			throw new SiteWhereException("CATALINA_HOME not set.");
		}
		File catFolder = new File(catalina);
		if (!catFolder.exists()) {
			throw new SiteWhereException("CATALINA_HOME folder does not exist.");
		}
		File confDir = new File(catalina, "conf");
		if (!confDir.exists()) {
			throw new SiteWhereException("CATALINA_HOME conf folder does not exist.");
		}
		File sitewhereDir = new File(confDir, "sitewhere");
		if (!confDir.exists()) {
			throw new SiteWhereException("CATALINA_HOME conf/sitewhere folder does not exist.");
		}
		return sitewhereDir;
	}
}