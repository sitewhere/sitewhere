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
import com.sitewhere.spi.configuration.IGlobalConfigurationResolver;

/**
 * Default implementation of {@link IGlobalConfigurationResolver}. Resolves via filesystem
 * relative to SiteWhere home directory.
 * 
 * @author Derek
 */
public class DefaultGlobalConfigurationResolver extends FileSystemGlobalConfigurationResolver {

	/** SiteWhere home directory */
	public static final String SITEWHERE_HOME = "sitewhere.home";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.configuration.FileSystemGlobalConfigurationResolver#
	 * getGlobalConfigurationFolder()
	 */
	@Override
	public File getGlobalConfigurationFolder() throws SiteWhereException {
		String sitewhere = System.getProperty(SITEWHERE_HOME);
		if (sitewhere == null) {
			throw new SiteWhereException("SITEWHERE_HOME not set.");
		}
		File swFolder = new File(sitewhere);
		if (!swFolder.exists()) {
			throw new SiteWhereException("SITEWHERE_HOME folder does not exist.");
		}
		File confDir = new File(swFolder, "conf");
		if (!confDir.exists()) {
			throw new SiteWhereException("SITEWHERE_HOME conf folder does not exist.");
		}
		return confDir;
	}
}