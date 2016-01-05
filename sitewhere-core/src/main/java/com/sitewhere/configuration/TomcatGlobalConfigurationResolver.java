/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.configuration.IGlobalConfigurationResolver;
import com.sitewhere.spi.system.IVersion;

/**
 * Resolves global configuration settings within a Tomcat instance.
 * 
 * @author Derek
 */
public class TomcatGlobalConfigurationResolver implements IGlobalConfigurationResolver {

	/** Static logger instance */
	public static Logger LOGGER = Logger.getLogger(TomcatGlobalConfigurationResolver.class);

	/** File name for SiteWhere global configuration file */
	public static final String GLOBAL_CONFIG_FILE_NAME = "sitewhere-server.xml";

	/** File name for SiteWhere state information in JSON format */
	public static final String STATE_FILE_NAME = "sitewhere-state.json";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.configuration.IConfigurationResolver#getGlobalConfiguration(com
	 * .sitewhere.spi.system.IVersion)
	 */
	@Override
	public byte[] getGlobalConfiguration(IVersion version) throws SiteWhereException {
		File sitewhereConf = getSiteWhereConfigFolder();
		File global = new File(sitewhereConf, GLOBAL_CONFIG_FILE_NAME);
		return getFileQuietly(global);
	}

	/**
	 * Get contents of a file as a byte array.
	 * 
	 * @param file
	 * @return
	 * @throws SiteWhereException
	 */
	public static byte[] getFileQuietly(File file) throws SiteWhereException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			FileInputStream in = new FileInputStream(file);
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			return out.toByteArray();
		} catch (FileNotFoundException e) {
			throw new SiteWhereException(e);
		} catch (IOException e) {
			throw new SiteWhereException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.configuration.IConfigurationResolver#resolveServerState(com.sitewhere
	 * .spi.system.IVersion)
	 */
	@Override
	public byte[] resolveServerState(IVersion version) throws SiteWhereException {
		File stateFile = new File(getSiteWhereConfigFolder(), STATE_FILE_NAME);
		if (!stateFile.exists()) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			FileInputStream in = new FileInputStream(stateFile);
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			return out.toByteArray();
		} catch (FileNotFoundException e) {
			throw new SiteWhereException(e);
		} catch (IOException e) {
			throw new SiteWhereException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.configuration.IConfigurationResolver#storeServerState(com.sitewhere
	 * .spi.system.IVersion, byte[])
	 */
	@Override
	public void storeServerState(IVersion version, byte[] data) throws SiteWhereException {
		File stateFile = new File(getSiteWhereConfigFolder(), STATE_FILE_NAME);
		if (!stateFile.exists()) {
			try {
				if (!stateFile.createNewFile()) {
					throw new SiteWhereException("Unable to create file for storing server state.");
				}
			} catch (IOException e) {
				throw new SiteWhereException("Unable to create file for storing server state: "
						+ stateFile.getAbsolutePath(), e);
			}
		}
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			FileOutputStream out = new FileOutputStream(stateFile);
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to save server state: " + stateFile.getAbsolutePath(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.configuration.IConfigurationResolver#getConfigurationRoot()
	 */
	@Override
	public URI getConfigurationRoot() throws SiteWhereException {
		return TomcatGlobalConfigurationResolver.getSiteWhereConfigFolder().toURI();
	}

	/**
	 * Gets the CATALINA/conf/sitewhere folder where configs are stored.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public static File getSiteWhereConfigFolder() throws SiteWhereException {
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

	/**
	 * Gets the CATALINA/data folder where data is stored.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public static File getSiteWhereDataFolder() throws SiteWhereException {
		String catalina = System.getProperty("catalina.base");
		if (catalina == null) {
			throw new SiteWhereException("CATALINA_HOME not set.");
		}
		File catFolder = new File(catalina);
		if (!catFolder.exists()) {
			throw new SiteWhereException("CATALINA_HOME folder does not exist.");
		}
		File dataDir = new File(catalina, "data");
		if (!dataDir.exists()) {
			dataDir.mkdir();
		}
		return dataDir;
	}
}