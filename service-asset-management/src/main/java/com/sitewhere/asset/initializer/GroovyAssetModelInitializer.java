/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.initializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.asset.spi.IAssetModelInitializer;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.rest.model.asset.request.scripting.AssetManagementRequestBuilder;
import com.sitewhere.server.ModelInitializer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetResolver;

import groovy.lang.Binding;

/**
 * Implementation of {@link IAssetModelInitializer} that delegates creation
 * logic to a Groovy script.
 * 
 * @author Derek
 */
public class GroovyAssetModelInitializer extends ModelInitializer implements IAssetModelInitializer {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Tenant Groovy configuration */
    private GroovyConfiguration groovyConfiguration;

    /** Relative path to Groovy script */
    private String scriptPath;

    public GroovyAssetModelInitializer(GroovyConfiguration groovyConfiguration, String scriptPath) {
	this.groovyConfiguration = groovyConfiguration;
	this.scriptPath = scriptPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.asset.spi.IAssetModelInitializer#initialize(com.sitewhere.
     * spi.asset.IAssetResolver)
     */
    @Override
    public void initialize(IAssetResolver assetResolver) throws SiteWhereException {
	// Skip if not enabled.
	if (!isEnabled()) {
	    return;
	}

	Binding binding = new Binding();
	binding.setVariable("logger", LOGGER);
	binding.setVariable("assetBuilder", new AssetManagementRequestBuilder(assetResolver));

	try {
	    getGroovyConfiguration().run(getScriptPath(), binding);
	} catch (SiteWhereException e) {
	    throw new SiteWhereException("Unable to run asset model initializer.", e);
	}
    }

    public GroovyConfiguration getGroovyConfiguration() {
	return groovyConfiguration;
    }

    public void setGroovyConfiguration(GroovyConfiguration groovyConfiguration) {
	this.groovyConfiguration = groovyConfiguration;
    }

    public String getScriptPath() {
	return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
	this.scriptPath = scriptPath;
    }
}