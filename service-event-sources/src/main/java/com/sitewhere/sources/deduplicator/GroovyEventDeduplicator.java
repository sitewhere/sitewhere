/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.deduplicator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;

import com.sitewhere.groovy.IGroovyVariables;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.rest.model.device.event.request.scripting.DeviceEventRequestBuilder;
import com.sitewhere.rest.model.device.request.scripting.DeviceManagementRequestBuilder;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.communication.EventDecodeException;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.device.communication.IDeviceEventDeduplicator;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.tenant.ITenant;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Implementation of {@link IDeviceEventDeduplicator} that uses a Groovy script
 * to decide whether an event is a duplicate or not. The Groovy script should
 * return a boolean value.
 * 
 * @author Derek
 */
public class GroovyEventDeduplicator extends TenantLifecycleComponent implements IDeviceEventDeduplicator {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Groovy configuration */
    private GroovyConfiguration groovyConfiguration;

    /** Path to script used for decoder */
    private String scriptPath;

    public GroovyEventDeduplicator() {
	super(LifecycleComponentType.DeviceEventDeduplicator);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceEventDeduplicator#
     * isDuplicate(com.sitewhere.spi.device.communication.IDecodedDeviceRequest)
     */
    @Override
    public boolean isDuplicate(IDecodedDeviceRequest<?> request) throws SiteWhereException {
	try {
	    Binding binding = new Binding();
	    binding.setVariable(IGroovyVariables.VAR_DEVICE_MANAGEMENT_BUILDER,
		    new DeviceManagementRequestBuilder(getDeviceManagement(getTenant())));
	    binding.setVariable(IGroovyVariables.VAR_EVENT_MANAGEMENT_BUILDER, new DeviceEventRequestBuilder(
		    getDeviceManagement(getTenant()), getDeviceEventManagement(getTenant())));
	    binding.setVariable(IGroovyVariables.VAR_DECODED_DEVICE_REQUEST, request);
	    binding.setVariable(IGroovyVariables.VAR_LOGGER, LOGGER);
	    LOGGER.debug("About to execute '" + getScriptPath() + "' for event request: " + request);
	    Boolean isDuplicate = (Boolean) getGroovyConfiguration().getGroovyScriptEngine().run(getScriptPath(),
		    binding);
	    return isDuplicate;
	} catch (ResourceException e) {
	    throw new EventDecodeException("Unable to access Groovy deduplicator script.", e);
	} catch (ScriptException e) {
	    throw new EventDecodeException("Unable to run Groovy deduplicator script.", e);
	} catch (CompilationFailedException e) {
	    throw new EventDecodeException("Error compiling Groovy script.", e);
	} catch (Throwable e) {
	    throw new EventDecodeException("Unhandled exception in Groovy deduplicator script.", e);
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

    private IDeviceManagement getDeviceManagement(ITenant tenant) {
	return null;
    }

    private IDeviceEventManagement getDeviceEventManagement(ITenant tenant) {
	return null;
    }
}