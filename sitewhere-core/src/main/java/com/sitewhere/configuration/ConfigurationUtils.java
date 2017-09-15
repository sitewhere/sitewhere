/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.InputStreamResource;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.system.IVersion;

/**
 * Utility class for managing server configuration.
 * 
 * @author Derek
 */
public class ConfigurationUtils {

    /**
     * Builds a Spring {@link ApplicationContext} from a byte array containing
     * the XML configuration.
     * 
     * @param configuration
     * @param version
     * @return
     * @throws SiteWhereException
     */
    public static ApplicationContext buildGlobalContext(byte[] configuration, IVersion version)
	    throws SiteWhereException {
	GenericApplicationContext context = new GenericApplicationContext();

	// Plug in custom property source.
	Map<String, Object> properties = new HashMap<String, Object>();
	properties.put("sitewhere.edition", version.getEditionIdentifier().toLowerCase());

	MapPropertySource source = new MapPropertySource("sitewhere", properties);
	context.getEnvironment().getPropertySources().addLast(source);

	// Read context from XML configuration file.
	XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
	reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
	reader.loadBeanDefinitions(new InputStreamResource(new ByteArrayInputStream(configuration)));

	context.refresh();
	return context;
    }

    /**
     * Build a Spring {@link ApplicationContext} from a byte[] containing a
     * tenant configuration. The context will inherit from the global context.
     * 
     * @param configuration
     * @param version
     * @param global
     * @return
     * @throws SiteWhereException
     */
    public static ApplicationContext buildSubcontext(byte[] configuration, IVersion version, ApplicationContext global)
	    throws SiteWhereException {
	GenericApplicationContext context = new GenericApplicationContext(global);

	// Plug in custom property source.
	Map<String, Object> properties = new HashMap<String, Object>();
	properties.put("sitewhere.edition", version.getEditionIdentifier().toLowerCase());

	MapPropertySource source = new MapPropertySource("sitewhere", properties);
	context.getEnvironment().getPropertySources().addLast(source);

	// Read context from XML configuration file.
	XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
	reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
	reader.loadBeanDefinitions(new InputStreamResource(new ByteArrayInputStream(configuration)));

	context.refresh();
	return context;
    }
}