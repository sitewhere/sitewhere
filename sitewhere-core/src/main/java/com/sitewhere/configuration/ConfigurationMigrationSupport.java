/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.configuration.IGlobalConfigurationResolver;

/**
 * Helps with migrating from previous configuration layout.
 * 
 * @author Derek
 */
public class ConfigurationMigrationSupport {

	/** Filenames for previous default tenant configurations */
	public static final String[] OLD_TEMPLATE_FILENAMES = { "sitewhere-tenant.xml", "tenant-template.xml" };

	/**
	 * Detects whether using old configuration model and upgrades if necessary.
	 * 
	 * @param global
	 * @throws SiteWhereException
	 */
	@SuppressWarnings("unchecked")
	public static void migrateIfNecessary(IGlobalConfigurationResolver global) throws SiteWhereException {
		File root = new File(global.getConfigurationRoot());
		if (!root.exists()) {
			throw new SiteWhereException("Configuration root does not exist.");
		}
		File templateFolder =
				new File(root, TomcatTenantConfigurationResolver.DEFAULT_TENANT_TEMPLATE_FOLDER);
		if (!templateFolder.exists()) {
			if (!templateFolder.mkdir()) {
				throw new SiteWhereException("Unable to create template folder.");
			}
			for (String filename : OLD_TEMPLATE_FILENAMES) {
				File templateFile = new File(root, filename);
				if (templateFile.exists()) {
					migrateTemplateFile(root, templateFolder, filename);
					migrateResources(root, templateFolder);
					break;
				}
			}
		}

		// Migrate tenant configuration files to separate directories.
		File tenants = new File(root, TomcatTenantConfigurationResolver.TENANTS_FOLDER);
		if (!tenants.exists()) {
			if (!tenants.mkdir()) {
				throw new SiteWhereException("Unable to create tenant resources folder.");
			}
		}
		Collection<File> oldConfigs =
				FileUtils.listFiles(root, FileFilterUtils.suffixFileFilter("-tenant.xml"), null);
		for (File oldConfig : oldConfigs) {
			int dash = oldConfig.getName().lastIndexOf('-');
			String tenantName = oldConfig.getName().substring(0, dash);
			File tenantFolder = new File(tenants, tenantName);
			try {
				FileUtils.copyDirectory(templateFolder, tenantFolder);
				File tenantConfig =
						new File(tenantFolder,
								TomcatTenantConfigurationResolver.DEFAULT_TENANT_CONFIGURATION_FILE + "."
										+ TomcatTenantConfigurationResolver.TENANT_SUFFIX_ACTIVE);
				if (tenantConfig.exists()) {
					tenantConfig.delete();
				}
				FileUtils.moveFile(oldConfig, tenantConfig);
				oldConfig.delete();
			} catch (IOException e) {
				throw new SiteWhereException("Unable to copy template folder for tenant.");
			}
		}
	}

	/**
	 * Move template file and rename it.
	 * 
	 * @param root
	 * @param templateFolder
	 * @param templateFilename
	 * @throws SiteWhereException
	 */
	protected static void migrateTemplateFile(File root, File templateFolder, String templateFilename)
			throws SiteWhereException {
		try {
			File templateFile = new File(root, templateFilename);
			FileUtils.moveFileToDirectory(templateFile, templateFolder, false);
			File moved = new File(templateFolder, templateFilename);
			File updated =
					new File(templateFolder,
							TomcatTenantConfigurationResolver.DEFAULT_TENANT_CONFIGURATION_FILE + "."
									+ TomcatTenantConfigurationResolver.TENANT_SUFFIX_ACTIVE);
			FileUtils.moveFile(moved, updated);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to move template file: " + templateFilename);
		}
	}

	/**
	 * Migrate assets and scripts into template directory.
	 * 
	 * @param root
	 * @param templateFolder
	 * @throws SiteWhereException
	 */
	protected static void migrateResources(File root, File templateFolder) throws SiteWhereException {
		// Move assets.
		File assets = new File(root, "assets");
		if (assets.exists()) {
			try {
				File newAssets = new File(templateFolder, TomcatTenantConfigurationResolver.ASSETS_FOLDER);
				FileUtils.moveDirectory(assets, newAssets);
			} catch (IOException e) {
				throw new SiteWhereException("Unable to move assets folder to template.");
			}
		}

		// Move Groovy scripts.
		File scripts = new File(templateFolder, TomcatTenantConfigurationResolver.SCRIPTS_FOLDER);
		if (!scripts.exists()) {
			if (!scripts.mkdir()) {
				throw new SiteWhereException("Unable to create scripts folder in template folder.");
			}
			try {
				File groovy = new File(root, "groovy");
				if (groovy.exists()) {
					File newGroovy = new File(scripts, "groovy");
					FileUtils.moveDirectory(groovy, newGroovy);
				}
			} catch (IOException e) {
				throw new SiteWhereException("Unable to move assets folder to template.");
			}
		}
	}
}