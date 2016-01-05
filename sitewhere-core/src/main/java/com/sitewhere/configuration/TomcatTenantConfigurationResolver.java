/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.configuration.ITenantConfigurationResolver;
import com.sitewhere.spi.system.IVersion;
import com.sitewhere.spi.user.ITenant;

/**
 * Resolves tenant configuration settings within a Tomcat instance.
 * 
 * @author Derek
 */
public class TomcatTenantConfigurationResolver implements ITenantConfigurationResolver {

	/** Static logger instance */
	public static Logger LOGGER = Logger.getLogger(TomcatTenantConfigurationResolver.class);

	/** File name for template used to create new tenant configurations */
	public static final String DEFAULT_TENANT_CONFIG_FILE_NAME = "tenant-template.xml";

	/** Suffix for an active tenant configuration */
	public static final String TENANT_SUFFIX_ACTIVE = "xml";

	/** Suffix for a staged tenant configuration */
	public static final String TENANT_SUFFIX_STAGED = "staged";

	/** Suffix for a backup tenant configuration */
	public static final String TENANT_SUFFIX_BACKUP = "backup";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.configuration.IConfigurationResolver#getActiveTenantConfiguration
	 * (com.sitewhere.spi.user.ITenant, com.sitewhere.spi.system.IVersion)
	 */
	@Override
	public byte[] getActiveTenantConfiguration(ITenant tenant, IVersion version) throws SiteWhereException {
		File sitewhereConf = TomcatGlobalConfigurationResolver.getSiteWhereConfigFolder();
		File tenantConfigFile =
				getTenantConfigurationFile(sitewhereConf, tenant, version, TENANT_SUFFIX_ACTIVE);
		if (!tenantConfigFile.exists()) {
			return null;
		}
		return TomcatGlobalConfigurationResolver.getFileQuietly(tenantConfigFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.configuration.IConfigurationResolver#getStagedTenantConfiguration
	 * (com.sitewhere.spi.user.ITenant, com.sitewhere.spi.system.IVersion)
	 */
	@Override
	public byte[] getStagedTenantConfiguration(ITenant tenant, IVersion version) throws SiteWhereException {
		File sitewhereConf = TomcatGlobalConfigurationResolver.getSiteWhereConfigFolder();
		File tenantConfigFile =
				getTenantConfigurationFile(sitewhereConf, tenant, version, TENANT_SUFFIX_STAGED);
		if (!tenantConfigFile.exists()) {
			return null;
		}
		return TomcatGlobalConfigurationResolver.getFileQuietly(tenantConfigFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.configuration.IConfigurationResolver#stageTenantConfiguration
	 * (byte[], com.sitewhere.spi.user.ITenant, com.sitewhere.spi.system.IVersion)
	 */
	@Override
	public void stageTenantConfiguration(byte[] configuration, ITenant tenant, IVersion version)
			throws SiteWhereException {
		File sitewhereConf = TomcatGlobalConfigurationResolver.getSiteWhereConfigFolder();
		File tenantConfigFile =
				getTenantConfigurationFile(sitewhereConf, tenant, version, TENANT_SUFFIX_STAGED);
		try {
			if (!tenantConfigFile.exists()) {
				tenantConfigFile.createNewFile();
			}
			LOGGER.info("Staging tenant configuration to: " + tenantConfigFile.getAbsolutePath());
			ByteArrayInputStream in = new ByteArrayInputStream(configuration);
			FileOutputStream out = new FileOutputStream(tenantConfigFile);
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to stage tenant configuration.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.configuration.IConfigurationResolver#createDefaultTenantConfiguration
	 * (com.sitewhere.spi.user.ITenant, com.sitewhere.spi.system.IVersion)
	 */
	@Override
	public byte[] createDefaultTenantConfiguration(ITenant tenant, IVersion version)
			throws SiteWhereException {
		File sitewhere = TomcatGlobalConfigurationResolver.getSiteWhereConfigFolder();
		File tenantDefault = new File(sitewhere, DEFAULT_TENANT_CONFIG_FILE_NAME);
		if (!tenantDefault.exists()) {
			throw new SiteWhereException("Default tenant configuration not found at: "
					+ tenantDefault.getAbsolutePath());
		}
		LOGGER.info("Copying configuration from " + tenantDefault.getAbsolutePath() + ".");
		File activeTenantFile = getTenantConfigurationFile(sitewhere, tenant, version, TENANT_SUFFIX_ACTIVE);
		copyDefaultTenantConfiguration(tenantDefault, activeTenantFile);
		createTenantPropertiesFile(tenant, sitewhere);
		return TomcatGlobalConfigurationResolver.getFileQuietly(activeTenantFile);
	}

	/**
	 * Copy the default tenant configuration to initialize a new tenant.
	 * 
	 * @param defaultConfig
	 * @param tenantConfig
	 * @throws SiteWhereException
	 */
	protected void copyDefaultTenantConfiguration(File defaultConfig, File tenantConfig)
			throws SiteWhereException {
		try {
			// Copy the default configuration to the tenant configuration.
			tenantConfig.createNewFile();
			FileInputStream in = new FileInputStream(defaultConfig);
			FileOutputStream out = new FileOutputStream(tenantConfig);
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to copy tenant configuration file: "
					+ defaultConfig.getAbsolutePath(), e);
		}
	}

	/**
	 * Create a default properties file for a tenant.
	 * 
	 * @param tenant
	 * @param folder
	 * @throws SiteWhereException
	 */
	protected void createTenantPropertiesFile(ITenant tenant, File folder) throws SiteWhereException {
		File tenantPropsFile = new File(folder, tenant.getId() + "-tenant.properties");
		try {
			tenantPropsFile.createNewFile();
			String content = "# Properties for '" + tenant.getName() + "' tenant configuration.\n";
			ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes());
			FileOutputStream out = new FileOutputStream(tenantPropsFile);
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		} catch (IOException e) {
			LOGGER.error("Unable to copy tenant configuration file: " + tenantPropsFile.getAbsolutePath(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.configuration.IConfigurationResolver#
	 * transitionStagedToActiveTenantConfiguration(com.sitewhere.spi.user.ITenant,
	 * com.sitewhere.spi.system.IVersion)
	 */
	@Override
	public void transitionStagedToActiveTenantConfiguration(ITenant tenant, IVersion version)
			throws SiteWhereException {
		File sitewhere = TomcatGlobalConfigurationResolver.getSiteWhereConfigFolder();
		File staged = getTenantConfigurationFile(sitewhere, tenant, version, TENANT_SUFFIX_STAGED);
		File active = getTenantConfigurationFile(sitewhere, tenant, version, TENANT_SUFFIX_ACTIVE);
		try {
			FileInputStream in = new FileInputStream(staged);
			FileOutputStream out = new FileOutputStream(active);
			LOGGER.info("Copying tenant configuration from " + staged.getAbsolutePath() + " to "
					+ active.getAbsolutePath() + ".");
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to move tenant configuration from staged to active.", e);
		}
		if (!staged.delete()) {
			throw new SiteWhereException("Unable to delete staged tenant configuration.");
		}
	}

	/**
	 * Get the tenant configuration file. Create one from the template if necessary.
	 * 
	 * @param sitewhereConf
	 * @param tenant
	 * @param version
	 * @return
	 * @throws SiteWhereException
	 */
	protected File getTenantConfigurationFile(File sitewhereConf, ITenant tenant, IVersion version,
			String suffix) throws SiteWhereException {
		return new File(sitewhereConf, tenant.getId() + "-tenant." + suffix);
	}
}