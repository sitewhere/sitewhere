/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.groovy.routing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.connectors.spi.routing.IRouteBuilder;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Uses Groovy script to build routes for event processor routing.
 * 
 * @author Derek
 */
public class GroovyRouteBuilder extends TenantEngineLifecycleComponent implements IRouteBuilder<String> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Groovy configuration */
    private GroovyConfiguration groovyConfiguration;

    /** Relative path to Groovy script */
    private String scriptPath;

    public GroovyRouteBuilder() {
	super(LifecycleComponentType.OutboundEventProcessorFilter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.processor.routing.IRouteBuilder#build(com.
     * sitewhere .spi.device.event.IDeviceEvent,
     * com.sitewhere.spi.device.IDevice,
     * com.sitewhere.spi.device.IDeviceAssignment)
     */
    @Override
    public String build(IDeviceEvent event, IDevice device, IDeviceAssignment assignment) throws SiteWhereException {
	Binding binding = new Binding();
	binding.setVariable("logger", LOGGER);
	binding.setVariable("event", event);
	binding.setVariable("device", device);
	binding.setVariable("assignment", assignment);
	try {
	    Object result = getGroovyConfiguration().getGroovyScriptEngine().run(getScriptPath(), binding);
	    if (!(result instanceof String)) {
		throw new SiteWhereException("Groovy route builder expected script to return a String.");
	    }
	    return (String) result;
	} catch (ResourceException e) {
	    throw new SiteWhereException("Unable to access Groovy route builder script.", e);
	} catch (ScriptException e) {
	    throw new SiteWhereException("Unable to run Groovy route builder script.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
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