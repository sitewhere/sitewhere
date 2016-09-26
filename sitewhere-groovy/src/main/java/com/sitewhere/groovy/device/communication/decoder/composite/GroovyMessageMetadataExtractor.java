package com.sitewhere.groovy.device.communication.decoder.composite;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;

import com.sitewhere.SiteWhere;
import com.sitewhere.groovy.GroovyConfiguration;
import com.sitewhere.groovy.device.communication.IGroovyVariables;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.EventDecodeException;
import com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder.IMessageMetadata;
import com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder.IMessageMetadataExtractor;
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

    /** Injected global Groovy configuration */
    private GroovyConfiguration configuration;

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
    public IMessageMetadata<byte[]> extractMetadata(byte[] payload, Map<String, String> eventSourceMetadata)
	    throws EventDecodeException {
	try {
	    Binding binding = new Binding();
	    binding.setVariable(IGroovyVariables.VAR_DEVICE_MANAGEMENT,
		    SiteWhere.getServer().getDeviceManagement(getTenant()));
	    binding.setVariable(IGroovyVariables.VAR_PAYLOAD, payload);
	    binding.setVariable(IGroovyVariables.VAR_PAYLOAD_METADATA, eventSourceMetadata);
	    binding.setVariable(IGroovyVariables.VAR_LOGGER, LOGGER);
	    LOGGER.debug("About to execute '" + getScriptPath() + "' with payload: " + payload);
	    return (IMessageMetadata<byte[]>) getConfiguration().getGroovyScriptEngine().run(getScriptPath(), binding);
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
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
     */
    @Override
    public void start() throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
     */
    @Override
    public void stop() throws SiteWhereException {
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

    public GroovyConfiguration getConfiguration() {
	return configuration;
    }

    public void setConfiguration(GroovyConfiguration configuration) {
	this.configuration = configuration;
    }

    public String getScriptPath() {
	return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
	this.scriptPath = scriptPath;
    }
}