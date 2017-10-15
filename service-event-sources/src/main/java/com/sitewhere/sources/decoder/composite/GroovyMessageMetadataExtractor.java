/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.decoder.composite;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;

import com.sitewhere.SiteWhere;
import com.sitewhere.groovy.IGroovyVariables;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.EventDecodeException;
import com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder.IMessageMetadata;
import com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder.IMessageMetadataExtractor;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Implements {@link IMessageMetadataExtractor} by using a Groovy script to
 * extract message metadata from a binary payload.
 * 
 * @author Derek
 */
public class GroovyMessageMetadataExtractor extends TenantLifecycleComponent
	implements IMessageMetadataExtractor<byte[]> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Groovy configuration */
    private GroovyConfiguration groovyConfiguration;

    /** Path to script used for decoder */
    private String scriptPath;

    public GroovyMessageMetadataExtractor() {
	super(LifecycleComponentType.Other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder.
     * IMessageMetadataExtractor#extractMetadata(java.lang.Object,
     * java.util.Map)
     */
    @Override
    @SuppressWarnings("unchecked")
    public IMessageMetadata<byte[]> extractMetadata(byte[] payload, Map<String, Object> eventSourceMetadata)
	    throws EventDecodeException {
	try {
	    Binding binding = new Binding();
	    binding.setVariable(IGroovyVariables.VAR_DEVICE_MANAGEMENT,
		    SiteWhere.getServer().getDeviceManagement(getTenant()));
	    binding.setVariable(IGroovyVariables.VAR_PAYLOAD, payload);
	    binding.setVariable(IGroovyVariables.VAR_PAYLOAD_METADATA, eventSourceMetadata);
	    binding.setVariable(IGroovyVariables.VAR_LOGGER, LOGGER);
	    LOGGER.debug("About to execute '" + getScriptPath() + "' with payload: " + payload);
	    return (IMessageMetadata<byte[]>) getGroovyConfiguration().getGroovyScriptEngine().run(getScriptPath(),
		    binding);
	} catch (ResourceException e) {
	    throw new EventDecodeException("Unable to access Groovy metadata extractor script.", e);
	} catch (ScriptException e) {
	    throw new EventDecodeException("Unable to run Groovy metadata extractor script.", e);
	} catch (CompilationFailedException e) {
	    throw new EventDecodeException("Error compiling Groovy metadata extractor script.", e);
	} catch (Throwable e) {
	    throw new EventDecodeException("Unhandled exception in Groovy metadata extractor script.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
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