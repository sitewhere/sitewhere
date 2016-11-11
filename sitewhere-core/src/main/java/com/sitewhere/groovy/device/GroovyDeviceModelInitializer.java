/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.groovy.device;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.rest.model.asset.request.scripting.AssetManagementRequestBuilder;
import com.sitewhere.rest.model.device.event.request.scripting.DeviceEventRequestBuilder;
import com.sitewhere.rest.model.device.request.scripting.DeviceManagementRequestBuilder;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.server.device.IDeviceModelInitializer;
import com.sitewhere.spi.server.groovy.ITenantGroovyConfiguration;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Implementation of {@link IDeviceModelInitializer} that delegates creation
 * logic to a Groovy script.
 * 
 * @author Derek
 */
public class GroovyDeviceModelInitializer implements IDeviceModelInitializer {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Tenant Groovy configuration */
    private ITenantGroovyConfiguration groovyConfiguration;

    /** Relative path to Groovy script */
    private String scriptPath;

    public GroovyDeviceModelInitializer(ITenantGroovyConfiguration groovyConfiguration, String scriptPath) {
	this.groovyConfiguration = groovyConfiguration;
	this.scriptPath = scriptPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.device.IDeviceModelInitializer#initialize(com.
     * sitewhere.spi.device.IDeviceManagement,
     * com.sitewhere.spi.device.event.IDeviceEventManagement,
     * com.sitewhere.spi.asset.IAssetManagement,
     * com.sitewhere.spi.asset.IAssetModuleManager)
     */
    @Override
    public void initialize(IDeviceManagement deviceManagement, IDeviceEventManagement deviceEventManagement,
	    IAssetManagement assetManagement, IAssetModuleManager assetModuleManager) throws SiteWhereException {
	Binding binding = new Binding();
	binding.setVariable("logger", LOGGER);
	binding.setVariable("deviceBuilder", new DeviceManagementRequestBuilder(deviceManagement));
	binding.setVariable("eventBuilder", new DeviceEventRequestBuilder(deviceManagement, deviceEventManagement));
	binding.setVariable("assetBuilder", new AssetManagementRequestBuilder(assetManagement, assetModuleManager));

	try {
	    // Use system account for logging "created by" on created elements.
	    SecurityContextHolder.getContext().setAuthentication(SiteWhereServer.getSystemAuthentication());
	    getGroovyConfiguration().getGroovyScriptEngine().run(getScriptPath(), binding);
	} catch (ResourceException e) {
	    throw new SiteWhereException("Unable to access Groovy script. " + e.getMessage(), e);
	} catch (ScriptException e) {
	    throw new SiteWhereException("Unable to run Groovy script.", e);
	} finally {
	    SecurityContextHolder.getContext().setAuthentication(null);
	}
    }

    public ITenantGroovyConfiguration getGroovyConfiguration() {
	return groovyConfiguration;
    }

    public void setGroovyConfiguration(ITenantGroovyConfiguration groovyConfiguration) {
	this.groovyConfiguration = groovyConfiguration;
    }

    public String getScriptPath() {
	return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
	this.scriptPath = scriptPath;
    }
}