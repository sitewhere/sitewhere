/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.initializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.device.spi.initializer.IDeviceModelInitializer;
import com.sitewhere.groovy.IGroovyVariables;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.rest.model.asset.request.scripting.AssetManagementRequestBuilder;
import com.sitewhere.rest.model.device.event.request.scripting.DeviceEventRequestBuilder;
import com.sitewhere.rest.model.device.request.scripting.DeviceManagementRequestBuilder;
import com.sitewhere.server.ModelInitializer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceEventManagement;

import groovy.lang.Binding;

/**
 * Implementation of {@link IDeviceModelInitializer} that delegates creation
 * logic to a Groovy script.
 * 
 * @author Derek
 */
public class GroovyDeviceModelInitializer extends ModelInitializer implements IDeviceModelInitializer {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(GroovyDeviceModelInitializer.class);

    /** Tenant Groovy configuration */
    private GroovyConfiguration groovyConfiguration;

    /** Relative path to Groovy script */
    private String scriptPath;

    public GroovyDeviceModelInitializer(GroovyConfiguration groovyConfiguration, String scriptPath) {
	this.groovyConfiguration = groovyConfiguration;
	this.scriptPath = scriptPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.spi.initializer.IDeviceModelInitializer#initialize(
     * com.sitewhere.spi.device.IDeviceManagement,
     * com.sitewhere.spi.device.event.IDeviceEventManagement,
     * com.sitewhere.spi.asset.IAssetManagement,
     * com.sitewhere.spi.asset.IAssetResolver)
     */
    @Override
    public void initialize(IDeviceManagement deviceManagement, IDeviceEventManagement deviceEventManagement,
	    IAssetResolver assetResolver) throws SiteWhereException {
	// Skip if not enabled.
	if (!isEnabled()) {
	    return;
	}

	Binding binding = new Binding();
	binding.setVariable(IGroovyVariables.VAR_LOGGER, LOGGER);
	binding.setVariable(IGroovyVariables.VAR_DEVICE_MANAGEMENT_BUILDER,
		new DeviceManagementRequestBuilder(deviceManagement));
	binding.setVariable(IGroovyVariables.VAR_EVENT_MANAGEMENT_BUILDER,
		new DeviceEventRequestBuilder(deviceManagement, deviceEventManagement));
	binding.setVariable(IGroovyVariables.VAR_ASSET_MANAGEMENT_BUILDER,
		new AssetManagementRequestBuilder(assetResolver));

	try {
	    getGroovyConfiguration().run(getScriptPath(), binding);
	} catch (SiteWhereException e) {
	    throw new SiteWhereException("Unable to run device model initializer. " + e.getMessage(), e);
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