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
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.log4j.Logger;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.configuration.IGlobalConfigurationResolver;

/**
 * Helps with migrating from previous configuration layout.
 * 
 * @author Derek
 */
public class ConfigurationMigrationSupport {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(ConfigurationMigrationSupport.class);

	/** Filenames for previous default tenant configurations */
	public static final String[] OLD_TEMPLATE_FILENAMES = { "sitewhere-tenant.xml", "tenant-template.xml" };

	/**
	 * Detects whether using old configuration model and upgrades if necessary.
	 * 
	 * @param global
	 * @throws SiteWhereException
	 */
	@SuppressWarnings("unchecked")
	public static void migrateProjectStructureIfNecessary(IGlobalConfigurationResolver global)
			throws SiteWhereException {
		File root = new File(global.getConfigurationRoot());
		if (!root.exists()) {
			throw new SiteWhereException("Configuration root does not exist.");
		}
		File templateFolder =
				new File(root, FileSystemTenantConfigurationResolver.DEFAULT_TENANT_TEMPLATE_FOLDER);
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
		File tenants = new File(root, FileSystemTenantConfigurationResolver.TENANTS_FOLDER);
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
								FileSystemTenantConfigurationResolver.DEFAULT_TENANT_CONFIGURATION_FILE + "."
										+ FileSystemTenantConfigurationResolver.TENANT_SUFFIX_ACTIVE);
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
							FileSystemTenantConfigurationResolver.DEFAULT_TENANT_CONFIGURATION_FILE + "."
									+ FileSystemTenantConfigurationResolver.TENANT_SUFFIX_ACTIVE);
			if (!moved.getAbsolutePath().equals(updated.getAbsolutePath())) {
				FileUtils.moveFile(moved, updated);
			}
		} catch (IOException e) {
			throw new SiteWhereException("Unable to migrate template file.", e);
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
				File newAssets = new File(templateFolder, FileSystemTenantConfigurationResolver.ASSETS_FOLDER);
				FileUtils.moveDirectory(assets, newAssets);
			} catch (IOException e) {
				throw new SiteWhereException("Unable to move assets folder to template.");
			}
		}

		// Move Groovy scripts.
		File scripts = new File(templateFolder, FileSystemTenantConfigurationResolver.SCRIPTS_FOLDER);
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

	/**
	 * Migrates a tenant configuration file to latest schema.
	 * 
	 * @param original
	 * @return
	 * @throws SiteWhereException
	 */
	public static byte[] migrateTenantConfigurationIfNecessary(byte[] original) throws SiteWhereException {
		Document document = getConfigurationDocument(original);

		migrateTenantConfiguration(document);

		String updated = format(document);
		return updated.getBytes();
	}

	/**
	 * Perform the migration work.
	 * 
	 * @param document
	 * @throws SiteWhereException
	 */
	protected static void migrateTenantConfiguration(Document document) throws SiteWhereException {
		Element beans = document.getDocumentElement();
		Element config = DomUtils.getChildElementByTagName(beans, "tenant-configuration");
		if (config == null) {
			throw new SiteWhereException("Tenant configuration element not found.");
		}
		Element dcomm = DomUtils.getChildElementByTagName(config, "device-communication");
		if (dcomm == null) {
			throw new SiteWhereException("Device communication element missing.");
		}
		Element asset = DomUtils.getChildElementByTagName(config, "asset-management");
		if (asset == null) {
			throw new SiteWhereException("Asset management element missing.");
		}
		Element eproc = DomUtils.getChildElementByTagName(config, "event-processing");
		if (eproc == null) {
			LOGGER.info("Event processing element missing. Migrating to new configuration format.");
			eproc = document.createElementNS(config.getNamespaceURI(), "event-processing");
			eproc.setPrefix(config.getPrefix());
			config.insertBefore(eproc, asset);

			moveEventProcessingElements(config, dcomm, eproc, document);
		}
		document.normalizeDocument();
	}

	/**
	 * Moves event processing elements from previous locations into new element.
	 * 
	 * @param config
	 * @param dcomm
	 * @param eproc
	 * @param document
	 * @throws SiteWhereException
	 */
	protected static void moveEventProcessingElements(Element config, Element dcomm, Element eproc,
			Document document) throws SiteWhereException {
		Element iproc = DomUtils.getChildElementByTagName(dcomm, "inbound-processing-strategy");
		if (iproc != null) {
			dcomm.removeChild(iproc);
			eproc.appendChild(iproc);
		}
		Element ichain = DomUtils.getChildElementByTagName(config, "inbound-processing-chain");
		if (ichain != null) {
			config.removeChild(ichain);
			eproc.appendChild(ichain);
		}
		Element oproc = DomUtils.getChildElementByTagName(dcomm, "outbound-processing-strategy");
		if (oproc != null) {
			dcomm.removeChild(oproc);
			eproc.appendChild(oproc);
		}
		Element ochain = DomUtils.getChildElementByTagName(config, "outbound-processing-chain");
		if (ochain != null) {
			config.removeChild(ochain);
			eproc.appendChild(ochain);
		}
		Element reg = DomUtils.getChildElementByTagName(dcomm, "registration");
		if (reg != null) {
			String qname =
					(reg.getPrefix() != null) ? (reg.getPrefix() + ":" + "device-services")
							: "device-services";
			document.renameNode(reg, reg.getNamespaceURI(), qname);
		}
	}

	/**
	 * Get configuration bytes as DOM document.
	 * 
	 * @param original
	 * @return
	 * @throws SiteWhereException
	 */
	protected static Document getConfigurationDocument(byte[] original) throws SiteWhereException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(new InputSource(new ByteArrayInputStream(original)));
		} catch (Exception e) {
			throw new SiteWhereException("Unable to parse tenant configuration.", e);
		}
	}

	/**
	 * Format the given XML document.
	 * 
	 * @param xml
	 * @return
	 * @throws SiteWhereException
	 */
	public static String format(Document xml) throws SiteWhereException {
		try {
			Transformer tf = TransformerFactory.newInstance().newTransformer();
			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			Writer out = new StringWriter();
			tf.transform(new DOMSource(xml), new StreamResult(out));
			return out.toString();
		} catch (Exception e) {
			throw new SiteWhereException("Unable to format XML document.", e);
		}
	}
}