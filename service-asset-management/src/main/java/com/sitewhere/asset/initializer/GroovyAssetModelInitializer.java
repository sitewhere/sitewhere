/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.initializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.asset.spi.IAssetModelInitializer;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.rest.model.asset.request.scripting.AssetManagementRequestBuilder;
import com.sitewhere.server.ModelInitializer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;

import groovy.lang.Binding;

/**
 * Implementation of {@link IAssetModelInitializer} that delegates creation
 * logic to a Groovy script.
 * 
 * @author Derek
 */
public class GroovyAssetModelInitializer extends ModelInitializer implements IAssetModelInitializer {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(GroovyAssetModelInitializer.class);

    /** Tenant Groovy configuration */
    private GroovyConfiguration groovyConfiguration;

    /** Relative path to Groovy script */
    private String scriptPath;

    public GroovyAssetModelInitializer(GroovyConfiguration groovyConfiguration, String scriptPath) {
	this.groovyConfiguration = groovyConfiguration;
	this.scriptPath = scriptPath;
    }

    /*
     * @see
     * com.sitewhere.asset.spi.IAssetModelInitializer#initialize(com.sitewhere.spi.
     * asset.IAssetManagement)
     */
    @Override
    public void initialize(IAssetManagement assetManagement) throws SiteWhereException {
	Binding binding = new Binding();
	binding.setVariable("logger", LOGGER);
	binding.setVariable("assetBuilder", new AssetManagementRequestBuilder(assetManagement));

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