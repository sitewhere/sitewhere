/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.groovy.device.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.groovy.GroovyConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.device.communication.IDeviceEventDecoder;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Implementation of {@link IDeviceEventDecoder} that delegates parsing of a JsonNode
 * payload to a Groovy script.
 * 
 * @author Derek
 */
public class GroovyJsonDecoder implements IDeviceEventDecoder<JsonNode> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(GroovyStringEventDecoder.class);

	/** Injected global Groovy configuration */
	private GroovyConfiguration configuration;

	/** Path to script used for decoder */
	private String scriptPath;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(java.lang.Object,
	 * java.util.Map)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<IDecodedDeviceRequest<?>> decode(JsonNode payload, Map<String, String> metadata)
			throws SiteWhereException {
		try {
			Binding binding = new Binding();
			List<IDecodedDeviceRequest<?>> events = new ArrayList<IDecodedDeviceRequest<?>>();
			binding.setVariable(IGroovyVariables.VAR_DECODED_EVENTS, events);
			binding.setVariable(IGroovyVariables.VAR_PAYLOAD, payload);
			binding.setVariable(IGroovyVariables.VAR_LOGGER, LOGGER);
			LOGGER.debug("About to execute '" + getScriptPath() + "' with payload: " + payload);
			getConfiguration().getGroovyScriptEngine().run(getScriptPath(), binding);
			return (List<IDecodedDeviceRequest<?>>) binding.getVariable(IGroovyVariables.VAR_DECODED_EVENTS);
		} catch (ResourceException e) {
			throw new SiteWhereException("Unable to access Groovy decoder script.", e);
		} catch (ScriptException e) {
			throw new SiteWhereException("Unable to run Groovy decoder script.", e);
		}
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