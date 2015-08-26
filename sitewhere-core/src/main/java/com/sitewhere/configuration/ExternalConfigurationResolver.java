/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
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
		LOGGER.info("Loading configuration from external source: " + getRemoteConfigUrl());
		try {
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
	 * com.sitewhere.spi.configuration.IConfigurationResolver#resolveTenantContext(com
	 * .sitewhere.spi.user.ITenant, com.sitewhere.spi.system.IVersion)
	 */
	@Override
	public ApplicationContext resolveTenantContext(ITenant tenant, IVersion version)
			throws SiteWhereException {
		// TODO: This does not make sense unless the configured URL is used as a base and
		// the tenant configuration is relative to it.
		return null;
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