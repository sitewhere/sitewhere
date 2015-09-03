/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.InputStreamResource;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.configuration.IConfigurationResolver;
import com.sitewhere.spi.system.IVersion;
import com.sitewhere.spi.user.ITenant;

/**
 * Allows SiteWhere configuration to be loaded from an external URL.
 * 
 * @author Derek
 */
public class ExternalConfigurationResolver implements IConfigurationResolver {

	/** Static logger instance */
	public static Logger LOGGER = Logger.getLogger(ExternalConfigurationResolver.class);

	/** URL used for loading configuration */
	private String remoteConfigUrl;

	public ExternalConfigurationResolver(String url) {
		this.remoteConfigUrl = url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.configuration.IConfigurationResolver#resolveSiteWhereContext(
	 * com.sitewhere.spi.system.IVersion)
	 */
	@Override
	public ApplicationContext resolveSiteWhereContext(IVersion version) throws SiteWhereException {
		try {
			String url = getRemoteConfigUrl() + "/sitewhere-server.xml";
			LOGGER.info("Loading configuration from external source: " + url);

			GenericApplicationContext context = new GenericApplicationContext();
			XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
			reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);

			URL remote = new URL(getRemoteConfigUrl());
			reader.loadBeanDefinitions(new InputStreamResource(remote.openStream()));

			context.refresh();
			return context;
		} catch (Exception e) {
			throw new SiteWhereException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.configuration.IConfigurationResolver#getTenantConfiguration(com
	 * .sitewhere.spi.user.ITenant, com.sitewhere.spi.system.IVersion)
	 */
	@Override
	public String getTenantConfiguration(ITenant tenant, IVersion version) throws SiteWhereException {
		URL remoteTenantUrl = getRemoteTenantUrl(tenant, version);
		try {
			InputStream in = remoteTenantUrl.openStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			return new String(out.toByteArray());
		} catch (IOException e) {
			throw new SiteWhereException("Unable to copy remote tenant configuration file: "
					+ remoteTenantUrl, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.configuration.IConfigurationResolver#resolveTenantContext(com
	 * .sitewhere.spi.user.ITenant, com.sitewhere.spi.system.IVersion,
	 * org.springframework.context.ApplicationContext)
	 */
	@Override
	public ApplicationContext resolveTenantContext(ITenant tenant, IVersion version, ApplicationContext parent)
			throws SiteWhereException {
		URL remoteTenantUrl = getRemoteTenantUrl(tenant, version);
		GenericApplicationContext context = new GenericApplicationContext(parent);

		// Plug in custom property source.
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("sitewhere.edition", version.getEditionIdentifier().toLowerCase());
		properties.put("tenant.id", tenant.getId());

		MapPropertySource source = new MapPropertySource("sitewhere", properties);
		context.getEnvironment().getPropertySources().addLast(source);

		try {
			// Read context from XML configuration file.
			XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
			reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
			reader.loadBeanDefinitions(new InputStreamResource(remoteTenantUrl.openStream()));
			context.refresh();
			return context;
		} catch (BeanDefinitionStoreException e) {
			throw new SiteWhereException(e);
		} catch (IOException e) {
			throw new SiteWhereException(e);
		}
	}

	/**
	 * Get URL for remote tenant configuration.
	 * 
	 * @param tenant
	 * @param version
	 * @return
	 * @throws SiteWhereException
	 */
	protected URL getRemoteTenantUrl(ITenant tenant, IVersion version) throws SiteWhereException {
		try {
			String remoteTenantUrl = getRemoteConfigUrl() + "/" + tenant.getId() + "-tenant.xml";
			URL remote = new URL(remoteTenantUrl);
			return remote;
		} catch (MalformedURLException e) {
			throw new SiteWhereException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.configuration.IConfigurationResolver#getConfigurationRoot()
	 */
	@Override
	public File getConfigurationRoot() throws SiteWhereException {
		return TomcatConfigurationResolver.getSiteWhereConfigFolder();
	}

	public String getRemoteConfigUrl() {
		return remoteConfigUrl;
	}

	public void setRemoteConfigUrl(String remoteConfigUrl) {
		this.remoteConfigUrl = remoteConfigUrl;
	}
}