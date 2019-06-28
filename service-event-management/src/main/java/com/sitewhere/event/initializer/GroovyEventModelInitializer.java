/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.initializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.event.spi.initializer.IEventModelInitializer;
import com.sitewhere.groovy.IGroovyVariables;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.rest.model.device.event.request.scripting.DeviceEventRequestBuilder;
import com.sitewhere.rest.model.device.request.scripting.DeviceManagementRequestBuilder;
import com.sitewhere.server.ModelInitializer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceEventManagement;

import groovy.lang.Binding;

/**
 * Implementation of {@link IEventModelInitializer} that delegates creation
 * logic to a Groovy script.
 */
public class GroovyEventModelInitializer extends ModelInitializer implements IEventModelInitializer {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(GroovyEventModelInitializer.class);

    /** Tenant Groovy configuration */
    private GroovyConfiguration groovyConfiguration;

    /** Relative path to Groovy script */
    private String scriptPath;

    public GroovyEventModelInitializer(GroovyConfiguration groovyConfiguration, String scriptPath) {
	this.groovyConfiguration = groovyConfiguration;
	this.scriptPath = scriptPath;
    }

    /*
     * @see
     * com.sitewhere.device.spi.initializer.IDeviceModelInitializer#initialize(com.
     * sitewhere.spi.device.IDeviceManagement,
     * com.sitewhere.spi.asset.IAssetManagement)
     */
    @Override
    public void initialize(IDeviceManagement deviceManagement, IDeviceEventManagement eventManagement)
	    throws SiteWhereException {
	Binding binding = new Binding();
	binding.setVariable(IGroovyVariables.VAR_LOGGER, LOGGER);
	binding.setVariable(IGroovyVariables.VAR_DEVICE_MANAGEMENT_BUILDER,
		new DeviceManagementRequestBuilder(deviceManagement));
	binding.setVariable(IGroovyVariables.VAR_EVENT_MANAGEMENT_BUILDER,
		new DeviceEventRequestBuilder(deviceManagement, eventManagement));

	try {
	    getGroovyConfiguration().run(getScriptPath(), binding);
	} catch (SiteWhereException e) {
	    throw new SiteWhereException("Unable to run event model initializer. " + e.getMessage(), e);
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
