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
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.configuration.IGlobalConfigurationResolver;
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

	/** Folder containing tenant resources */
	public static final String TENANTS_FOLDER = "tenants";

	/** Folder containing tenant asset resources */
	public static final String ASSETS_FOLDER = "assets";

	/** Folder containing tenant script resources */
	public static final String SCRIPTS_FOLDER = "scripts";

	/** Folder containing default tenant template information */
	public static final String DEFAULT_TENANT_TEMPLATE_FOLDER = "tenant-template";

	/** Filename for tenant configuration information */
	public static final String DEFAULT_TENANT_CONFIGURATION_FILE = "sitewhere-tenant";

	/** Suffix for an active tenant configuration */
	public static final String TENANT_SUFFIX_ACTIVE = "xml";

	/** Suffix for a staged tenant configuration */
	public static final String TENANT_SUFFIX_STAGED = "staged";

	/** Suffix for a backup tenant configuration */
	public static final String TENANT_SUFFIX_BACKUP = "backup";

	/** Tenant */
	private ITenant tenant;

	/** Version information */
	@SuppressWarnings("unused")
	private IVersion version;

	/** Global configuration resolver */
	private IGlobalConfigurationResolver globalConfigurationResolver;

	public TomcatTenantConfigurationResolver(ITenant tenant, IVersion version,
			IGlobalConfigurationResolver globalConfigurationResolver) {
		this.tenant = tenant;
		this.version = version;
		this.globalConfigurationResolver = globalConfigurationResolver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.configuration.ITenantConfigurationResolver#getAssetResourcesRoot
	 * ()
	 */
	@Override
	public URI getAssetResourcesRoot() throws SiteWhereException {
		File tenantFolder = getTenantFolder();
		return (new File(tenantFolder, ASSETS_FOLDER)).toURI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.configuration.ITenantConfigurationResolver#getScriptResourcesRoot
	 * ()
	 */
	@Override
	public URI getScriptResourcesRoot() throws SiteWhereException {
		File tenantFolder = getTenantFolder();
		return (new File(tenantFolder, SCRIPTS_FOLDER)).toURI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.configuration.ITenantConfigurationResolver#
	 * getActiveTenantConfiguration()
	 */
	@Override
	public byte[] getActiveTenantConfiguration() throws SiteWhereException {
		File tenantConfigFile = getTenantConfigurationFile(getTenantFolder(), TENANT_SUFFIX_ACTIVE);
		if (!tenantConfigFile.exists()) {
			return null;
		}
		return TomcatGlobalConfigurationResolver.getFileQuietly(tenantConfigFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.configuration.ITenantConfigurationResolver#
	 * getStagedTenantConfiguration()
	 */
	@Override
	public byte[] getStagedTenantConfiguration() throws SiteWhereException {
		File tenantConfigFile = getTenantConfigurationFile(getTenantFolder(), TENANT_SUFFIX_STAGED);
		if (!tenantConfigFile.exists()) {
			return null;
		}
		return TomcatGlobalConfigurationResolver.getFileQuietly(tenantConfigFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.configuration.ITenantConfigurationResolver#stageTenantConfiguration
	 * (byte[])
	 */
	@Override
	public void stageTenantConfiguration(byte[] configuration) throws SiteWhereException {
		File tenantConfigFile = getTenantConfigurationFile(getTenantFolder(), TENANT_SUFFIX_STAGED);
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
	 * com.sitewhere.spi.configuration.ITenantConfigurationResolver#hasValidConfiguration
	 * ()
	 */
	@Override
	public boolean hasValidConfiguration() {
		try {
			getTenantFolder();
			return true;
		} catch (SiteWhereException e) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.configuration.ITenantConfigurationResolver#
	 * createDefaultTenantConfiguration()
	 */
	@Override
	public byte[] createDefaultTenantConfiguration() throws SiteWhereException {
		File root = new File(getGlobalConfigurationResolver().getConfigurationRoot());
		if (!root.exists()) {
			throw new SiteWhereException("Global configuration root not found.");
		}
		File tenants = new File(root, TomcatTenantConfigurationResolver.TENANTS_FOLDER);
		if (!tenants.exists()) {
			throw new SiteWhereException("Unable to create tenant resources folder.");
		}
		File tenantFolder = new File(tenants, tenant.getId());
		File templateFolder =
				new File(root, TomcatTenantConfigurationResolver.DEFAULT_TENANT_TEMPLATE_FOLDER);
		if (!templateFolder.exists()) {
			throw new SiteWhereException("Tenant template folder not found.");
		}
		try {
			FileUtils.copyDirectory(templateFolder, tenantFolder);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to copy template folder for tenant.");
		}

		LOGGER.info("Copying new tenant from template at " + templateFolder.getAbsolutePath() + ".");
		File activeTenantFile = getTenantConfigurationFile(tenantFolder, TENANT_SUFFIX_ACTIVE);
		return TomcatGlobalConfigurationResolver.getFileQuietly(activeTenantFile);
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
	 * @see com.sitewhere.spi.configuration.ITenantConfigurationResolver#
	 * transitionStagedToActiveTenantConfiguration()
	 */
	@Override
	public void transitionStagedToActiveTenantConfiguration() throws SiteWhereException {
		File sitewhere = TomcatGlobalConfigurationResolver.getSiteWhereConfigFolder();
		File staged = getTenantConfigurationFile(sitewhere, TENANT_SUFFIX_STAGED);
		File active = getTenantConfigurationFile(sitewhere, TENANT_SUFFIX_ACTIVE);
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
	 * Get the tenant resources folder.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	protected File getTenantFolder() throws SiteWhereException {
		File root = new File(getGlobalConfigurationResolver().getConfigurationRoot());
		if (!root.exists()) {
			throw new SiteWhereException("Global configuration root not found.");
		}

		File tenants = new File(root, TENANTS_FOLDER);
		if (!tenants.exists()) {
			throw new SiteWhereException("Tenants folder not found.");
		}

		File tenantFolder = new File(tenants, tenant.getId());
		if (!tenantFolder.exists()) {
			throw new SiteWhereException("Tenant folder not found for '" + tenant.getId() + "'.");
		}

		return tenantFolder;
	}

	/**
	 * Get the tenant configuration file. Throw an exception if not found.
	 * 
	 * @param sitewhereConf
	 * @param suffix
	 * @return
	 * @throws SiteWhereException
	 */
	protected File getTenantConfigurationFile(File sitewhereConf, String suffix) throws SiteWhereException {
		return new File(getTenantFolder(), DEFAULT_TENANT_CONFIGURATION_FILE + "." + suffix);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.configuration.ITenantConfigurationResolver#
	 * getGlobalConfigurationResolver()
	 */
	public IGlobalConfigurationResolver getGlobalConfigurationResolver() {
		return globalConfigurationResolver;
	}

	public void setGlobalConfigurationResolver(IGlobalConfigurationResolver globalConfigurationResolver) {
		this.globalConfigurationResolver = globalConfigurationResolver;
	}
}