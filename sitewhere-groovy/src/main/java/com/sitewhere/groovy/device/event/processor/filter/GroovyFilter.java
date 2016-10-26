/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.groovy.device.event.processor.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.device.event.processor.filter.DeviceEventFilter;
import com.sitewhere.groovy.GroovyConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.processor.IDeviceEventFilter;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Implementatoin of {@link IDeviceEventFilter} that uses a Groovy script to
 * determine whether events should be included or filtered. If the script
 * returns true, the event is included. If the script return false, the event is
 * filtered.
 * 
 * @author Derek
 */
public class GroovyFilter extends DeviceEventFilter {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Injected global Groovy configuration */
    private GroovyConfiguration configuration;

    /** Relative path to Groovy script */
    private String scriptPath;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.event.processor.filter.DeviceEventFilter#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	if (getScriptPath() == null) {
	    throw new SiteWhereException("Script path not configured for Groovy filter.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.processor.IDeviceEventFilter#isFiltered(
     * com.sitewhere .spi.device.event.IDeviceEvent,
     * com.sitewhere.spi.device.IDevice,
     * com.sitewhere.spi.device.IDeviceAssignment)
     */
    @Override
    public boolean isFiltered(IDeviceEvent event, IDevice device, IDeviceAssignment assignment)
	    throws SiteWhereException {
	Binding binding = new Binding();
	binding.setVariable("logger", getLogger());
	binding.setVariable("event", event);
	binding.setVariable("device", device);
	binding.setVariable("assignment", assignment);

	try {
	    Object result = getConfiguration().getGroovyScriptEngine().run(getScriptPath(), binding);
	    if (!(result instanceof Boolean)) {
		throw new SiteWhereException("Groovy filter script returned non-boolean result.");
	    }
	    return !((Boolean) result).booleanValue();
	} catch (ResourceException e) {
	    throw new SiteWhereException("Unable to access Groovy filter script. " + e.getMessage(), e);
	} catch (ScriptException e) {
	    throw new SiteWhereException("Unable to run Groovy filter script.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.event.processor.filter.DeviceEventFilter#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public String getScriptPath() {
	return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
	this.scriptPath = scriptPath;
    }

    public GroovyConfiguration getConfiguration() {
	return configuration;
    }

    public void setConfiguration(GroovyConfiguration configuration) {
	this.configuration = configuration;
    }
}